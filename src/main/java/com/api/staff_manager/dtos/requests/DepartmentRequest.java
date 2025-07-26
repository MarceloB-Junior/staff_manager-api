package com.api.staff_manager.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(@NotBlank String name) {
    public DepartmentRequest {
        name = (name != null) ? name.trim() : null;
    }
}
