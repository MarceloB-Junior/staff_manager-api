package com.api.staff_manager.exceptions.custom;

public class FileLoadingException extends RuntimeException {
    public FileLoadingException(String message, Throwable cause){
        super(message, cause);
    }
}
