package com.api.staff_manager.exceptions.custom;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String message){
        super(message);
    }
}
