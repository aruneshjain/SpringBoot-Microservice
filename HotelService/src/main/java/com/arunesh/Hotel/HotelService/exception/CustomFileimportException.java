package com.arunesh.Hotel.HotelService.exception;

public class CustomFileimportException extends RuntimeException{
    public CustomFileimportException(String process){
        super(process);
    }
}