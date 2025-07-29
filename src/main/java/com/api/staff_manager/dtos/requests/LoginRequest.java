package com.api.staff_manager.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    public LoginRequest {
        email = (email != null) ? email.trim() : null;
        password = (password != null) ? password.trim() : null;
    }
}
