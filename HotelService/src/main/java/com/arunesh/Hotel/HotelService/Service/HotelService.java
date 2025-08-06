package com.arunesh.Hotel.HotelService.Service;

import com.arunesh.Hotel.HotelService.Entity.Hotel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface HotelService {
    ResponseEntity<String> saveHotel(Hotel hotel);

    ResponseEntity<List<Hotel>> getAllHotel();

    ResponseEntity<Hotel> getHotel(UUID id);
}
