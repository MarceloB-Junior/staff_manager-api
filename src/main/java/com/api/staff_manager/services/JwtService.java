package com.api.staff_manager.services;

import com.api.staff_manager.models.UserModel;

public interface JwtService {
    String generateAccessToken(UserModel user);
    String generateRefreshToken(UserModel user);
    String validateAccessToken(String token);
    String validateRefreshToken(String token);
    Long getExpirationTimeMillis(String token);
}
