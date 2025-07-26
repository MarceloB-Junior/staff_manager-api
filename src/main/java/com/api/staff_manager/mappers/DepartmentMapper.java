package com.api.staff_manager.mappers;

import com.api.staff_manager.dtos.requests.DepartmentRequest;
import com.api.staff_manager.dtos.responses.DepartmentDetailsResponse;
import com.api.staff_manager.dtos.responses.DepartmentViewResponse;
import com.api.staff_manager.models.DepartmentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface DepartmentMapper {
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DepartmentModel toEntity(DepartmentRequest request);

    DepartmentViewResponse toViewResponse(DepartmentModel department);

    DepartmentDetailsResponse toDetailsResponse(DepartmentModel department);
}