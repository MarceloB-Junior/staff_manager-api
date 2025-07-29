package com.api.staff_manager.dtos.responses;

import com.api.staff_manager.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailsResponse(
        @JsonProperty(value = "user_id")
        UUID userId,
        String name, String email, RoleEnum role,
        @JsonProperty(value = "created_at")
        LocalDateTime createdAt,
        @JsonProperty(value = "updated_at")
        LocalDateTime updatedAt
) {
}
