package com.api.staff_manager.services;

import com.api.staff_manager.dtos.requests.UserCreationRequest;
import com.api.staff_manager.dtos.requests.UserUpdateRequest;
import com.api.staff_manager.dtos.responses.UserDetailsResponse;
import com.api.staff_manager.dtos.responses.UserSummaryResponse;
import com.api.staff_manager.dtos.responses.UserViewResponse;
import com.api.staff_manager.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    UserSummaryResponse save(UserCreationRequest request);
    Page<UserViewResponse> findAll(Pageable pageable);
    UserDetailsResponse findById(UUID id);
    UserDetailsResponse findByEmail(String email);
    UserModel findModelByEmail(String email);
    UserDetailsResponse update(UserUpdateRequest request, UUID id);
    void delete(UUID id);
}
