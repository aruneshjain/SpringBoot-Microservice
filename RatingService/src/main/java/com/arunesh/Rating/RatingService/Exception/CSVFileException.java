package com.arunesh.Rating.RatingService.Exception;

public class CSVFileException extends RuntimeException{
    public CSVFileException(String process){
        super(process);
    }
}