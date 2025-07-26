package com.api.staff_manager.exceptions.custom;

public class FileDeletionException extends RuntimeException {
    public FileDeletionException(String message, Throwable cause){
        super(message, cause);
    }
}
