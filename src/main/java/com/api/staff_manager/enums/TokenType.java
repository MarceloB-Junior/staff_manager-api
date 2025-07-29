package com.api.staff_manager.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {
    ACCESS("access_token"), REFRESH("refresh_token");

    private final String tokenType;

    @Override
    public String toString() {
        return this.tokenType;
    }
}
