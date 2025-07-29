package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserSummaryResponse(@JsonProperty(value = "user_id") UUID userId, String name, String email) {
}
