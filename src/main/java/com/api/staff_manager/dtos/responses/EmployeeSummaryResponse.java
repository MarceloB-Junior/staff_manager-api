package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record EmployeeSummaryResponse(@JsonProperty(value = "employee_id") UUID employeeId, String name, String position){
}
