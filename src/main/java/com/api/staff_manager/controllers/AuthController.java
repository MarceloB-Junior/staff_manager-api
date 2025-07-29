package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.LoginRequest;
import com.api.staff_manager.dtos.responses.TokenResponse;
import com.api.staff_manager.models.UserModel;
import com.api.staff_manager.services.JwtService;
import com.api.staff_manager.services.UserService;
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
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> logout(){
        log.info("Request received to logout user");
        var cookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
