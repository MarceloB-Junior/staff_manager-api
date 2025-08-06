package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.LoginRequest;
import com.api.staff_manager.dtos.responses.TokenResponse;
import com.api.staff_manager.exceptions.dto.ApiError;
import com.api.staff_manager.models.UserModel;
import com.api.staff_manager.services.JwtService;
import com.api.staff_manager.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints related to authentication")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Authenticates user",
            description = "Authenticates user and return access token and refresh token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class)
                            ),
                            headers = @Header(
                                    name = "Set-Cookie",
                                    description = "Refresh token cookie with HttpOnly flag",
                                    schema = @Schema(
                                            type = "string",
                                            example = "refresh-token=eyJ...; HttpOnly; Path=/; Max-Age=86400")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request){
        log.info("Request received to login user with email {}", request.email());
        var authToken = new UsernamePasswordAuthenticationToken(request.email(),request.password());
        var authentication = authenticationManager.authenticate(authToken);

        var accessToken = jwtService.generateAccessToken((UserModel) authentication.getPrincipal());
        var refreshToken = jwtService.generateRefreshToken((UserModel) authentication.getPrincipal());

        var cookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(
                        TokenResponse.builder()
                                .accessToken(accessToken)
                                .expiresIn(jwtService.getExpirationTimeMillis(accessToken))
                                .build()
                );
    }

    @Operation(
            summary = "Refreshes user tokens",
            description = "Refresh user tokens using the refresh token present in the HttpOnly cookie named `refresh-token` " +
            "**Note:** This endpoint relies on an HttpOnly cookie and due to the limitations of the Swagger UI interface, " +
            "it cannot be tested directly here. Please use Postman, Insomnia, or make HTTP requests via a browser for testing."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully refresh user tokens",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PostMapping("/refresh-token")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TokenResponse> refreshToken(@CookieValue(value = "refresh-token") String refreshToken){
        log.info("Request received to refresh user tokens");
        var userEmail = jwtService.validateRefreshToken(refreshToken);
        var user = userService.findModelByEmail(userEmail);

        var accessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        var cookie = ResponseCookie.from("refresh-token", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(
                        TokenResponse.builder()
                                .accessToken(accessToken)
                                .expiresIn(jwtService.getExpirationTimeMillis(accessToken))
                                .build()
                );
    }

    @Operation(
            summary = "Logs out user",
            description = "Logs out user cleaning refresh token cookie  " +
                    "**Note:** This endpoint relies on an HttpOnly cookie and due to the limitations of the Swagger UI interface, " +
                    "it cannot be tested directly here. Please use Postman, Insomnia, or make HTTP requests via a browser for testing.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                  @ApiResponse(
                          responseCode = "204",
                          description = "User logged out successfully"
                  ),
                  @ApiResponse(
                          responseCode = "401",
                          description = "Unauthorized",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                  )
            }
    )
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> logout(){
        log.info("Request received to logout user");
        var cookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
