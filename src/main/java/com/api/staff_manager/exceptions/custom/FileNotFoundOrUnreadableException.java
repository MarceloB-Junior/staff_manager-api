package com.api.staff_manager.exceptions.custom;

public class FileNotFoundOrUnreadableException extends RuntimeException {
    public FileNotFoundOrUnreadableException(String message){
        super(message);
    }
}
