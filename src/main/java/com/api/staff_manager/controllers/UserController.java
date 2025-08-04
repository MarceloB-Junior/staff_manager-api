package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.UserCreationRequest;
import com.api.staff_manager.dtos.requests.UserUpdateRequest;
import com.api.staff_manager.dtos.responses.UserDetailsResponse;
import com.api.staff_manager.dtos.responses.UserSummaryResponse;
import com.api.staff_manager.dtos.responses.UserViewResponse;
import com.api.staff_manager.exceptions.dto.ApiError;
import com.api.staff_manager.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Get all users from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved users",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value =
                                    "{\"content\":[{\"user_id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"," +
                                            "\"name\":\"Anna Doe\",\"email\":\"anna.doe@example.com\"," +
                                            "\"role\":\"USER\"}],\"page\":{\"size\":10,\"number\":0," +
                                            "\"totalElements\":1,\"totalPages\":1}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserViewResponse>> getAllUsers(@ParameterObject
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable pageable){
        log.info("Request received to fetch all users");
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(
            summary = "Get one user by id",
            description = "Get user from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable(value = "id") UUID id){
        log.info("Request received to fetch a user by id {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(
            summary = "Get authenticated user details",
            description = "Get authenticated user details from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user details",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailsResponse> getUserDetails(Authentication authentication){
        log.info("Request received to fetch a user by email {}", authentication.getName());
        return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
    }

    @Operation(
            summary = "Create a new user",
            description = "Create a new user from the Staff Manager API"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserSummaryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserSummaryResponse> saveUser(@RequestBody @Valid UserCreationRequest request){
        log.info("Request received to create a new user with email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    @Operation(
            summary = "Update user",
            description = "Update user from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Another user with the same email already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponse> updateUser(@PathVariable(value = "id") UUID id,
                                                          @RequestBody @Valid UserUpdateRequest request){
        log.info("Request received to update the user with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(userService.update(request, id));
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted user"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete a user with id {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
