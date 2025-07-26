package com.api.staff_manager.exceptions.custom;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause){
        super(message, cause);
    }
}
