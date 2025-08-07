package com.api.staff_manager.dtos.responses;

import com.api.staff_manager.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserViewResponse(@JsonProperty(value = "user_id") UUID userId, String name, String email, RoleEnum role) {
}
