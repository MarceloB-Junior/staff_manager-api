package com.api.staff_manager.mappers;

import com.api.staff_manager.dtos.requests.EmployeeRequest;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.dtos.responses.EmployeeViewResponse;
import com.api.staff_manager.dtos.responses.EmployeeSummaryResponse;
import com.api.staff_manager.models.EmployeeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "employeePhoto", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmployeeModel toEntity(EmployeeRequest request);

    EmployeeSummaryResponse toSummaryResponse(EmployeeModel employee);

    @Mapping(target = "departmentId", source = "department.departmentId")
    EmployeeViewResponse toViewResponse(EmployeeModel employee);

    @Mapping(target = "departmentId", source = "department.departmentId")
    EmployeeDetailsResponse toDetailsResponse(EmployeeModel employee);
}