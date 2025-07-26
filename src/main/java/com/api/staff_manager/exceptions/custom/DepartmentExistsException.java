package com.api.staff_manager.exceptions.custom;

public class DepartmentExistsException extends RuntimeException {
    public DepartmentExistsException(String message) {
        super(message);
    }
}
