package com.arunesh.Rating.RatingService.Exception;

public class RatingNotFoundException extends RuntimeException{
   public RatingNotFoundException(String process){
        super(process);
    }
}
