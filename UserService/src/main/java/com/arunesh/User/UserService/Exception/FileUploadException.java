package com.arunesh.User.UserService.Exception;

public class FileUploadException extends RuntimeException{
    public FileUploadException(String process){
        super(process);
    }
}