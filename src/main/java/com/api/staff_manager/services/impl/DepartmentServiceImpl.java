package com.api.staff_manager.services.impl;

import com.api.staff_manager.dtos.requests.DepartmentRequest;
import com.api.staff_manager.dtos.responses.DepartmentDetailsResponse;
import com.api.staff_manager.dtos.responses.DepartmentViewResponse;
import com.api.staff_manager.exceptions.custom.DepartmentExistsException;
import com.api.staff_manager.exceptions.custom.DepartmentNotFoundException;
import com.api.staff_manager.mappers.DepartmentMapper;
import com.api.staff_manager.repositories.DepartmentRepository;
import com.api.staff_manager.services.DepartmentService;
import com.api.staff_manager.services.PhotoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final PhotoService photoService;

    @Override
    @Transactional
    public DepartmentViewResponse save(DepartmentRequest request) {
        log.debug("Trying to save department with request: {}", request);
        if(departmentRepository.existsByName(request.name())){
            log.error("Department with name '{}' already exists", request.name());
            throw new DepartmentExistsException("Department already exists.");
        }
        var department = departmentMapper.toEntity(request);
        var savedDepartment = departmentRepository.save(department);
        log.debug("Successfully save a department with request: {}", request);
        return departmentMapper.toViewResponse(savedDepartment);
    }

    @Override
    public Page<DepartmentViewResponse> findAll(Pageable pageable) {
        log.debug("Trying to find all departments with pageable: {}", pageable);
        return departmentRepository.findAll(pageable).map(departmentMapper::toViewResponse);
    }

    @Override
    public DepartmentDetailsResponse findById(UUID id) {
        log.debug("Trying to find a department with id: {}", id);
        var department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        return departmentMapper.toDetailsResponse(department);
    }

    @Override
    public DepartmentDetailsResponse update(DepartmentRequest request, UUID id) {
        log.debug("Trying to find a department with id: {} to update", id);
        var department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));

        if(departmentRepository.existsByName(request.name()) && !department.getName().equals(request.name())){
            log.error("Another department with name '{}' already exists", request.name());
            throw new DepartmentExistsException("Department already exists.");
        }
        department.setName(request.name());
        var updatedDepartment = departmentRepository.save(department);
        log.debug("Successfully update a department with id: {}", updatedDepartment.getDepartmentId());
        return departmentMapper.toDetailsResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Trying to find a department with id: {} to delete", id);
        var department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        department.getEmployees().forEach(employee -> {
            if(employee.getEmployeePhoto() != null){
                photoService.deletePhoto(employee.getEmployeeId());
            }
        });
        department.getEmployees().clear();
        departmentRepository.delete(department);
        log.debug("Successfully delete a department with id: {}", id);
    }
}
