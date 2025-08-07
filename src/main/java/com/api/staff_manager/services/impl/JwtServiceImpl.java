package com.api.staff_manager.services.impl;

import com.api.staff_manager.enums.TokenType;
import com.api.staff_manager.models.UserModel;
import com.api.staff_manager.services.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Log4j2
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${spring.application.name}")
    private String iss;

    @Value("${jwt.token.access.expiration}")
    private Integer accessExpiration;

    @Value("${jwt.token.refresh.expiration}")
    private Integer refreshExpiration;

    @Override
    public String generateAccessToken(UserModel user) {
        log.debug("Generate access token from user with email {}", user.getEmail());
        try {
            return JWT.create()
                    .withIssuer(iss)
                    .withSubject(user.getEmail())
                    .withClaim("type", TokenType.ACCESS.toString())
                    .withExpiresAt(generateExpirationDate(accessExpiration))
                    .sign(Algorithm.HMAC256(jwtSecret));
        } catch (JWTCreationException e){
            log.error("Error in generating access token from user with email {}", user.getEmail());
            throw new JWTCreationException("Error in generating access token: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateRefreshToken(UserModel user) {
        log.debug("Generate refresh token from user with email {}", user.getEmail());
        try {
            return JWT.create()
                    .withIssuer(iss)
                    .withSubject(user.getEmail())
                    .withClaim("type", TokenType.REFRESH.toString())
                    .withExpiresAt(generateExpirationDate(refreshExpiration))
                    .sign(Algorithm.HMAC256(jwtSecret));
        } catch (JWTCreationException e){
            log.error("Error in generating refresh token from user with email {}", user.getEmail());
            throw new JWTCreationException("Error in generating refresh token: " + e.getMessage(), e);
        }
    }

    @Override
    public String validateAccessToken(String token) {
        log.debug("Trying to validate access token");
        try {
            return JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(iss)
                    .withClaim("type",TokenType.ACCESS.toString())
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            log.error("Error in validate access token: {}", e.getMessage());
            throw new JWTVerificationException("Invalid access token: " + e.getMessage());
        }
    }

    @Override
    public String validateRefreshToken(String token) {
        log.debug("Trying to validate refresh token");
        try {
            return JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(iss)
                    .withClaim("type",TokenType.REFRESH.toString())
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            log.error("Error in validate refresh token: {}", e.getMessage());
            throw new JWTVerificationException("Invalid refresh token: " + e.getMessage());
        }
    }

    @Override
    public Long getExpirationTimeMillis(String token) {
        log.debug("Trying to get access token expiration");
        try {
            return JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(iss)
                    .withClaim("type", TokenType.ACCESS.toString())
                    .build()
                    .verify(token)
                    .getExpiresAt().getTime();
        } catch (JWTVerificationException e){
            log.error("Error in getting a access token expiration: {}", e.getMessage());
            throw new JWTVerificationException("Invalid access token: " + e.getMessage());
        }
    }

    private Instant generateExpirationDate(Integer expiration){
        return LocalDateTime.now().plusMinutes(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
