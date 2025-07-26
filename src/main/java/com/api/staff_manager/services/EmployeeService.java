package com.api.staff_manager.services;

import com.api.staff_manager.dtos.requests.EmployeeRequest;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.dtos.responses.EmployeeViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeService {
    EmployeeViewResponse save(EmployeeRequest request);
    Page<EmployeeViewResponse> findAll(Pageable pageable);
    EmployeeDetailsResponse findById(UUID id);
    EmployeeDetailsResponse update(EmployeeRequest request, UUID id);
    void delete(UUID id);
}