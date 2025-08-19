package com.arunesh.Hotel.HotelService.exception;

public class HotelNotFoundException extends RuntimeException{
   public HotelNotFoundException(String process){
        super(process);
    }
}
