package com.api.staff_manager.configs.security;

import com.api.staff_manager.exceptions.custom.UserNotFoundException;
import com.api.staff_manager.repositories.UserRepository;
import com.api.staff_manager.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var tokens = extractTokens(request);
        var accessToken = tokens.get("access-token");
        var refreshToken = tokens.get("refresh-token");

        if ("/api/v1/auth/refresh-token".equals(request.getRequestURI())){
            if(refreshToken != null){
                String loginEmail = jwtService.validateRefreshToken(refreshToken);
                var user = userRepository.findByEmail(loginEmail)
                        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginEmail));
                var authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),null,user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else {
            if(accessToken != null){
                String loginEmail = jwtService.validateAccessToken(accessToken);
                var user = userRepository.findByEmail(loginEmail)
                        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginEmail));
                var authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),null,user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Map<String,String> extractTokens(HttpServletRequest request){
        String accessToken = null, refreshToken = null;
        var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            accessToken = authorizationHeader.split(" ")[1];
        }

        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("refresh-token".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        var tokens = new HashMap<String,String>();
        tokens.put("access-token", accessToken);
        tokens.put("refresh-token", refreshToken);
        return tokens;
    }
}
