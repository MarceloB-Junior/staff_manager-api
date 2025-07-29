package com.api.staff_manager.controllers;

import com.api.staff_manager.dtos.requests.EmployeeRequest;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.dtos.responses.EmployeeViewResponse;
import com.api.staff_manager.services.PhotoService;
import com.api.staff_manager.services.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<EmployeeViewResponse>> getAllEmployees(
            @PageableDefault(sort = "employeeId", direction = Sort.Direction.ASC) Pageable pageable){
        log.info("Request received to fetch all employees");
        return ResponseEntity.ok(employeeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EmployeeDetailsResponse> getEmployeeById(@PathVariable(value = "id")UUID id){
        log.info("Request received to fetch an employee by id {}", id);
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping("/{id}/photo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> getEmployeePhotoById(@PathVariable(value = "id") UUID id,
                                                              HttpServletRequest servletRequest) throws IOException {
        log.info("Request received to fetch photo of employee with id {}", id);
        var resource = photoService.loadPhotoAsResource(id);
        var contentType = servletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeViewResponse> saveEmployee(@RequestBody @Valid EmployeeRequest request){
        log.info("Request received to create a new employee. Request body: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDetailsResponse> updateEmployee(@PathVariable(value = "id") UUID id,
                                                                  @RequestBody @Valid EmployeeRequest request){
        log.info("Request received to update the employee with id {}. Request body: {}", id, request);
        return ResponseEntity.ok(employeeService.update(request, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete an employee with id {}", id);
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDetailsResponse> uploadPhoto(@PathVariable(value = "id") UUID id,
                                                               @RequestParam("file") MultipartFile file){
        log.info("Request received to create a new photo to employee with id {}", id);
        photoService.storePhoto(id, file);
        var photoPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("api","v1","employees",String.valueOf(id),"photo").toUriString();
        return ResponseEntity.ok(photoService.associatePhotoToEmployee(id, photoPath));
    }

    @DeleteMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhotoByEmployeeId(@PathVariable(value = "id") UUID id){
        log.info("Request received to delete an employee photo with id {}", id);
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
