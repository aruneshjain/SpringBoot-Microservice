package com.arunesh.Hotel.HotelService.Exception;

public class HotelNotFoundException extends RuntimeException{
   public HotelNotFoundException(String process){
        super(process);
    }
}
