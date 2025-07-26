package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record DepartmentViewResponse(@JsonProperty(value = "department_id") UUID departmentId, String name) {
}