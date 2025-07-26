package com.api.staff_manager.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeRequest(
        @NotBlank
        String name,
        @NotBlank
        String position,
        @NotNull @Positive
        BigDecimal salary,
        @NotNull @JsonProperty(value = "department_id")
        UUID departmentId
) {
        public EmployeeRequest {
                name = (name != null) ? name.trim() : null;
                position = (position != null) ? position.trim() : null;
        }
}
