package com.api.staff_manager.exceptions.custom;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message){
        super(message);
    }
}
