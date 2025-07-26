package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DepartmentDetailsResponse(
        @JsonProperty(value = "department_id")
        UUID departmentId,
        String name,
        List<EmployeeSummaryResponse> employees,
        @JsonProperty(value = "created_at")
        LocalDateTime createdAt,
        @JsonProperty(value = "updated_at")
        LocalDateTime updatedAt
) {
}
