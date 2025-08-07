package com.api.staff_manager.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenResponse(@JsonProperty(value = "access_token") String accessToken,
                            @JsonProperty(value = "expires_in") Long expiresIn) {
}
