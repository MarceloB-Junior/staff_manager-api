package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.DepartmentRequest;
import com.api.staff_manager.dtos.responses.DepartmentDetailsResponse;
import com.api.staff_manager.dtos.responses.DepartmentViewResponse;
import com.api.staff_manager.exceptions.dto.ApiError;
import com.api.staff_manager.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
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

    @Operation(
            summary = "Get all departments",
            description = "Get all departments from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved departments",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value =
                                    "{\"content\":[{\"department_id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"," +
                                            "\"name\":\"string\"}],\"page\":{\"size\":10,\"number\":0,\"totalElements\":1," +
                                            "\"totalPages\":1}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DepartmentViewResponse>> getAllDepartments(@ParameterObject
             @PageableDefault(sort = "departmentId", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Request received to fetch all departments");
        return ResponseEntity.ok(departmentService.findAll(pageable));
    }

    @Operation(
            summary = "Get one department by id",
            description = "Get a department from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved department",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DepartmentDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Department not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DepartmentDetailsResponse> getDepartmentById(@PathVariable(value = "id") UUID id) {
        log.info("Request received to fetch a department by id {}", id);
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @Operation(
            summary = "Create a new department",
            description = "Create a new department from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created a department",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DepartmentViewResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Department already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentViewResponse> saveDepartment(@RequestBody @Valid DepartmentRequest request) {
        log.info("Request received to create a new department. Request body: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.save(request));
    }

    @Operation(
            summary = "Update a department",
            description = "Update a department from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated a department",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DepartmentDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Another department with the same name already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Department not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDetailsResponse> updateDepartment(@PathVariable(value = "id") UUID id,
                                                                      @RequestBody @Valid DepartmentRequest request) {
        log.info("Request received to update the department with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(departmentService.update(request, id));
    }

    @Operation(
            summary = "Delete a department",
            description = "Delete a department from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted a department"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Department not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable(value = "id") UUID id) {
        log.info("Request received to delete a department with id {}", id);
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}