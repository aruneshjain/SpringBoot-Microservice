package com.arunesh.User.UserService.Exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String process){
        super(process);
    }
}
