package com.arunesh.User.UserService.Exception;

public class UserNotFoundException extends RuntimeException{
   public UserNotFoundException(String process){
        super(process);
    }
}
