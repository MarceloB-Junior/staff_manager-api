package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeViewResponse(
        @JsonProperty(value = "employee_id")
        UUID employeeId,
        String name, String position, BigDecimal salary,
        @JsonProperty(value = "department_id")
        UUID departmentId
) {
}
