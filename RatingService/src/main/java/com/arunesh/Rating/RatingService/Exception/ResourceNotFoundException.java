package com.arunesh.Rating.RatingService.Exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String process){
        super(process);
    }
}
