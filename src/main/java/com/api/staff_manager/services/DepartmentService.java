package com.api.staff_manager.services;

import com.api.staff_manager.dtos.requests.DepartmentRequest;
import com.api.staff_manager.dtos.responses.DepartmentDetailsResponse;
import com.api.staff_manager.dtos.responses.DepartmentViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DepartmentService {
    DepartmentViewResponse save(DepartmentRequest request);
    Page<DepartmentViewResponse> findAll(Pageable pageable);
    DepartmentDetailsResponse findById(UUID id);
    DepartmentDetailsResponse update(DepartmentRequest request, UUID id);
    void delete(UUID id);
}
