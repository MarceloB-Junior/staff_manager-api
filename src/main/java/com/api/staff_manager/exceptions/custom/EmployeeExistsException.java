package com.api.staff_manager.exceptions.custom;

public class EmployeeExistsException extends RuntimeException {
    public EmployeeExistsException(String message) {
        super(message);
    }
}
