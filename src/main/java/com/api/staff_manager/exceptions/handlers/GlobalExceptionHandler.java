package com.api.staff_manager.exceptions.handlers;

import com.api.staff_manager.exceptions.custom.*;
import com.api.staff_manager.exceptions.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DepartmentExistsException.class)
    public ResponseEntity<ApiError> departmentExistsHandler(DepartmentExistsException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiError> departmentNotFoundHandler(DepartmentNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(EmployeeExistsException.class)
    public ResponseEntity<ApiError> employeeExistsHandler(EmployeeExistsException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> employeeNotFoundHandler(EmployeeNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(UnsupportedFileException.class)
    public ResponseEntity<ApiError> unsupportedFileHandler(UnsupportedFileException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(FileNotFoundOrUnreadableException.class)
    public ResponseEntity<ApiError> fileNotFoundOrUnreadableHandler(FileNotFoundOrUnreadableException e,
                                                                    HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericHandler(Exception e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message("An unexpected error has occurred.")
                        .path(request.getRequestURI())
                        .build()
        );
    }

}
