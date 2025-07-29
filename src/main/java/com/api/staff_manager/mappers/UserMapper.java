package com.api.staff_manager.mappers;

import com.api.staff_manager.dtos.requests.UserCreationRequest;
import com.api.staff_manager.dtos.responses.UserDetailsResponse;
import com.api.staff_manager.dtos.responses.UserSummaryResponse;
import com.api.staff_manager.dtos.responses.UserViewResponse;
import com.api.staff_manager.models.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserModel toEntity(UserCreationRequest request);

    UserSummaryResponse toSummaryResponse(UserModel user);

    UserViewResponse toViewResponse(UserModel user);

    UserDetailsResponse toDetailsResponse(UserModel user);
}