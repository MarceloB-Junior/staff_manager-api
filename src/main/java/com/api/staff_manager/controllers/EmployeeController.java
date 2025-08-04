package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.EmployeeRequest;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.dtos.responses.EmployeeViewResponse;
import com.api.staff_manager.exceptions.dto.ApiError;
import com.api.staff_manager.services.PhotoService;
import com.api.staff_manager.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PhotoService photoService;

    @Operation(
            summary = "Get all employees",
            description = "Get all employees from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved employees",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value =
                                    "{\"content\":[{\"employee_id\":\"f89ef3d9-0fe4-488f-b228-8addbb326db3\"," +
                                            "\"name\":\"string\",\"position\":\"string\"," +
                                            "\"salary\":7500.00," +
                                            "\"department_id\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"}]," +
                                            "\"page\":{\"size\":10,\"number\":0,\"totalElements\":1,\"totalPages\":1}}"
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
    public ResponseEntity<Page<EmployeeViewResponse>> getAllEmployees(@ParameterObject
            @PageableDefault(sort = "employeeId", direction = Sort.Direction.ASC) Pageable pageable){
        log.info("Request received to fetch all employees");
        return ResponseEntity.ok(employeeService.findAll(pageable));
    }

    @Operation(
            summary = "Get one employee by id",
            description = "Get an employee from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
    public ResponseEntity<EmployeeDetailsResponse> getEmployeeById(@PathVariable(value = "id")UUID id){
        log.info("Request received to fetch an employee by id {}", id);
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Operation(
            summary = "Get an employee photo by id",
            description = "Get an employee photo from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved employee photo",
                            content = {
                                    @Content(mediaType = "image/png"),
                                    @Content(mediaType = "image/jpeg"),
                                    @Content(mediaType = "image/jpg")
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @GetMapping("/{id}/photo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> getEmployeePhotoById(@PathVariable(value = "id") UUID id,
                                                              HttpServletRequest servletRequest) throws IOException {
        log.info("Request received to fetch photo of employee with id {}", id);
        var resource = photoService.loadPhotoAsResource(id);
        var contentType = servletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    @Operation(
            summary = "Create a new employee",
            description = "Create a new employee from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created an employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeViewResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Employee already exists",
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeViewResponse> saveEmployee(@RequestBody @Valid EmployeeRequest request){
        log.info("Request received to create a new employee. Request body: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(request));
    }

    @Operation(
            summary = "Update an employee",
            description = "Update an employee from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated an employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Another employee with the same name already exists in department",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee/Department not found",
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
    public ResponseEntity<EmployeeDetailsResponse> updateEmployee(@PathVariable(value = "id") UUID id,
                                                                  @RequestBody @Valid EmployeeRequest request){
        log.info("Request received to update the employee with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(employeeService.update(request, id));
    }

    @Operation(
            summary = "Delete an employee",
            description = "Delete an employee from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted an employee"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
    public ResponseEntity<Void> deleteEmployee(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete an employee with id {}", id);
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Upload an employee photo",
            description = "Create a new employee photo from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created an employee photo"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(
                            responseCode = "415",
                            description = "Unsupported file type",
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
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDetailsResponse> uploadPhoto(@PathVariable(value = "id") UUID id,
                                                               @RequestParam("file") MultipartFile file){
        log.info("Request received to create a new photo to employee with id {}", id);
        photoService.storePhoto(id, file);
        var photoPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("api","v1","employees",String.valueOf(id),"photo").toUriString();
        return ResponseEntity.ok(photoService.associatePhotoToEmployee(id, photoPath));
    }

    @Operation(
            summary = "Delete an employee photo",
            description = "Delete an employee from the Staff Manager API",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted an employee photo"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
    @DeleteMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhotoByEmployeeId(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete an employee photo with id {}", id);
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
