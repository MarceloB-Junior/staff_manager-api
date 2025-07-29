package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.UserCreationRequest;
import com.api.staff_manager.dtos.requests.UserUpdateRequest;
import com.api.staff_manager.dtos.responses.UserDetailsResponse;
import com.api.staff_manager.dtos.responses.UserSummaryResponse;
import com.api.staff_manager.dtos.responses.UserViewResponse;
import com.api.staff_manager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserViewResponse>> getAllUsers(
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable pageable){
        log.info("Request received to fetch all users");
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable(value = "id") UUID id){
        log.info("Request received to fetch a user by id {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailsResponse> getUserDetails(Authentication authentication){
        log.info("Request received to fetch a user by email {}", authentication.getName());
        return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserSummaryResponse> saveUser(@RequestBody @Valid UserCreationRequest request){
        log.info("Request received to create a new user with email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponse> updateUser(@PathVariable(value = "id") UUID id,
                                                          @RequestBody @Valid UserUpdateRequest request){
        log.info("Request received to update the user with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(userService.update(request, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete a user with id {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
