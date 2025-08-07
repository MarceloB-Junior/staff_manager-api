package com.api.staff_manager.dtos.requests;

import com.api.staff_manager.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(@NotBlank String name, @Email @NotBlank String email, @NotNull RoleEnum role) {
    public UserUpdateRequest {
        name = (name != null) ? name.trim() : null;
        email = (email != null) ? email.trim() : null;
    }
}