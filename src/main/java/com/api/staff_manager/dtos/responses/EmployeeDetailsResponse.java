package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeDetailsResponse(
        @JsonProperty(value = "employee_id")
        UUID employeeId,
        String name, String position, BigDecimal salary,
        @JsonProperty(value = "employee_photo")
        String employeePhoto,
        @JsonProperty(value = "department_id")
        UUID departmentId,
        @JsonProperty(value = "created_at")
        LocalDateTime createdAt,
        @JsonProperty(value = "updated_at")
        LocalDateTime updatedAt
) {
}
