package com.api.staff_manager.services.impl;

import com.api.staff_manager.configs.FileStorageConfig;
import com.api.staff_manager.dtos.responses.EmployeeDetailsResponse;
import com.api.staff_manager.exceptions.custom.*;
import com.api.staff_manager.mappers.EmployeeMapper;
import com.api.staff_manager.repositories.EmployeeRepository;
import com.api.staff_manager.services.PhotoService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
public class PhotoServiceImpl implements PhotoService {

    private final EmployeeMapper employeeMapper;
    private final Path pathLocation;
    private final EmployeeRepository employeeRepository;

    public PhotoServiceImpl(EmployeeMapper employeeMapper,
                            FileStorageConfig storageConfig, EmployeeRepository employeeRepository) {
        this.employeeMapper = employeeMapper;
        this.pathLocation = Paths.get(storageConfig.getUploadDir()).toAbsolutePath().normalize();
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void storePhoto(UUID employeeId, MultipartFile file) {
        log.debug("Storing photo for employee with id: {}", employeeId);
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (!List.of("image/png", "image/jpeg", "image/jpg").contains(file.getContentType())) {
            log.error("Unsupported file type: {}", file.getContentType());
            throw new UnsupportedFileException("Unsupported file type.");
        }
        var filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename().replaceAll("\\s+", "-")));
        if (filename.contains("..")) {
            log.error("Filename contains invalid path sequence: {}", filename);
            throw new UnsupportedFileException("Filename contains invalid path sequence: " + filename);
        }

        try {
            if (employee.getEmployeePhoto() != null) {
                log.debug("Deleting existing photo for employee id: {}", employeeId);
                deletePhoto(employeeId);
            }
            var employeeDir = Files.createDirectories(pathLocation.resolve(String.valueOf(employeeId)).normalize());
            var targetLocation = employeeDir.resolve(filename);
            file.transferTo(targetLocation);
            log.debug("Photo stored successfully at {}", targetLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file.", e);
        }
    }

    @Override
    @Transactional
    public EmployeeDetailsResponse associatePhotoToEmployee(UUID employeeId, String photoUrl) {
        log.debug("Associating photo URL to employee with id: {}", employeeId);
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        employee.setEmployeePhoto(photoUrl);
        var savedEmployee = employeeRepository.save(employee);
        log.debug("Photo associated successfully to employee id: {}", savedEmployee.getEmployeeId());
        return employeeMapper.toDetailsResponse(savedEmployee);
    }

    @Override
    public Resource loadPhotoAsResource(UUID employeeId) {
        log.debug("Loading photo resource for employee with id: {}", employeeId);
        var employeeDir = getEmployeeDirPath(employeeId);

        try (var walk = Files.walk(employeeDir)) {
            var photoPathOptional = walk.filter(Files::isRegularFile).findFirst();
            return photoPathOptional.map(path -> {
                try {
                    var normalizedPath = path.normalize();
                    var resource = new UrlResource(normalizedPath.toUri());
                    if (resource.exists() && resource.isReadable()){
                        log.debug("Photo resource loaded successfully for employee id: {}", employeeId);
                        return resource;
                    } else {
                        log.error("File not found or cannot be read: {}", normalizedPath);
                        throw new FileNotFoundOrUnreadableException("File not found or cannot be read: " + normalizedPath);
                    }
                } catch (MalformedURLException e) {
                    log.error("Error loading file for employee id: {}. Error: {}", employeeId, e.getMessage());
                    throw new FileLoadingException("Error loading file: " + e.getMessage(), e);
                }
            })
            .orElseThrow(() -> new FileNotFoundOrUnreadableException("No photo file found for employee: " + employeeId));
        } catch (IOException e) {
            log.error("Error walking file tree for employee id: {}. Error: {}", employeeId, e.getMessage());
            throw new FileTreeWalkException("Error walking file tree. " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePhoto(UUID employeeId) {
        log.debug("Deleting photo for employee with id: {}", employeeId);
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        var employeeDir = getEmployeeDirPath(employeeId);

        try (var walk = Files.walk(employeeDir)) {
            var photoPath = walk.filter(Files::isRegularFile).findFirst();
            photoPath.ifPresent(path -> {
                try {
                    Files.deleteIfExists(path);
                    Files.deleteIfExists(employeeDir);
                    log.debug("Photo file and directory deleted for employee id: {}", employeeId);
                } catch (IOException e) {
                    log.error("Failed to delete file for employee id: {}. Error: {}", employeeId, e.getMessage());
                    throw new FileDeletionException("Failed to delete file: " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            log.error("Error walking file tree for deletion for employee id: {}. Error: {}", employeeId, e.getMessage());
            throw new FileTreeWalkException("Error walking file tree: " + e.getMessage(), e);
        }
        employee.setEmployeePhoto(null);
        employeeRepository.save(employee);
        log.debug("Photo association removed from employee id: {}", employeeId);
    }

    private Path getEmployeeDirPath(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException("Employee not found with id: " + employeeId);
        }
        return pathLocation.resolve(String.valueOf(employeeId)).normalize();
    }
}
