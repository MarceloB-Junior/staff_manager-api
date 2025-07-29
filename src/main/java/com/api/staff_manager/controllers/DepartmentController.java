package com.api.staff_manager.controllers;


import com.api.staff_manager.dtos.requests.DepartmentRequest;
import com.api.staff_manager.dtos.responses.DepartmentDetailsResponse;
import com.api.staff_manager.dtos.responses.DepartmentViewResponse;
import com.api.staff_manager.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DepartmentViewResponse>> getAllDepartments(
            @PageableDefault(sort = "departmentId", direction = Sort.Direction.ASC) Pageable pageable){
        log.info("Request received to fetch all departments");
        return ResponseEntity.ok(departmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DepartmentDetailsResponse> getDepartmentById(@PathVariable(value = "id") UUID id){
        log.info("Request received to fetch a department by id {}", id);
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentViewResponse> saveDepartment(@RequestBody @Valid DepartmentRequest request){
        log.info("Request received to create a new department. Request body: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.save(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDetailsResponse> updateDepartment(@PathVariable(value = "id")UUID id,
                                                                   @RequestBody @Valid DepartmentRequest request){
        log.info("Request received to update the department with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(departmentService.update(request, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete a department with id {}", id);
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}