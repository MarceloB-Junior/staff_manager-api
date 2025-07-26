package com.api.staff_manager.services;

import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PhotoService {
    void storePhoto(UUID employeeId, MultipartFile file);
    EmployeeDetailsResponse associatePhotoToEmployee(UUID employeeId, String photoUrl);
    Resource loadPhotoAsResource(UUID employeeId);
    void deletePhoto(UUID employeeId);
}
