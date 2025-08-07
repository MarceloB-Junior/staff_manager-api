package com.api.staff_manager.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreationRequest(@NotBlank String name, @NotBlank String password, @Email @NotBlank String email) {
    public UserCreationRequest {
        name = (name != null) ? name.trim() : null;
        password = (password != null) ? password.trim() : null;
        email = (email != null) ? email.trim() : null;
    }
}
