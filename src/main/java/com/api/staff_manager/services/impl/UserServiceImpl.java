package com.api.staff_manager.services.impl;

import com.api.staff_manager.dtos.requests.UserCreationRequest;
import com.api.staff_manager.dtos.requests.UserUpdateRequest;
import com.api.staff_manager.dtos.responses.UserDetailsResponse;
import com.api.staff_manager.dtos.responses.UserSummaryResponse;
import com.api.staff_manager.dtos.responses.UserViewResponse;
import com.api.staff_manager.enums.RoleEnum;
import com.api.staff_manager.exceptions.custom.InvalidRoleException;
import com.api.staff_manager.exceptions.custom.UserExistsException;
import com.api.staff_manager.exceptions.custom.UserNotFoundException;
import com.api.staff_manager.mappers.UserMapper;
import com.api.staff_manager.models.UserModel;
import com.api.staff_manager.repositories.UserRepository;
import com.api.staff_manager.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserSummaryResponse save(UserCreationRequest request) {
        log.debug("Trying to save user with email: {}", request.email());
        if (userRepository.existsByEmail(request.email())){
            log.error("User with email address '{}' already exists", request.email());
            throw new UserExistsException("A user with the provided email address already exists.");
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(RoleEnum.USER);

        var savedUser = userRepository.save(user);
        log.debug("Successfully saved user with email: {}", request.email());
        return userMapper.toSummaryResponse(savedUser);
    }

    @Override
    public Page<UserViewResponse> findAll(Pageable pageable) {
        log.debug("Trying to find all users with pageable: {}", pageable);
        return userRepository.findAll(pageable).map(userMapper::toViewResponse);
    }

    @Override
    public UserDetailsResponse findById(UUID id) {
        log.debug("Trying to find a user with id: {}", id);
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDetailsResponse(user);
    }

    @Override
    public UserDetailsResponse findByEmail(String email) {
        log.debug("Trying to find a user with email: {}", email);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toDetailsResponse(user);
    }

    @Override
    public UserModel findModelByEmail(String email) {
        log.debug("Trying to find a user model with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetailsResponse update(UserUpdateRequest request, UUID id) {
        log.debug("Trying to find a user with id: {} to update", id);
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if (userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email())){
            log.error("Another user with email address '{}' already exists", request.email());
            throw new UserExistsException("A user with the provided email address already exists.");
        }
        if (!List.of(RoleEnum.ADMIN,RoleEnum.USER).contains(request.role())){
            log.error("Invalid role '{}' received in user update request", request.role());
            throw new InvalidRoleException("Invalid role received: "+ request.role() + ". Allowed roles: ADMIN, USER.");
        }
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        var updatedUser = userRepository.save(user);
        log.debug("Successfully update a user with id: {}", updatedUser.getUserId());
        return userMapper.toDetailsResponse(updatedUser);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Trying to find a user with id: {} to delete", id);
        if (!userRepository.existsById(id)){
            log.error("User not found with id {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.debug("Successfully delete a user with id: {}", id);
    }
}
