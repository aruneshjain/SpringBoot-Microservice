package com.arunesh.Hotel.HotelService.service;

import com.arunesh.Hotel.HotelService.entity.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

public interface HotelService {
    ResponseEntity<Hotel> saveHotel(Hotel hotel);

    ResponseEntity<List<Hotel>> getAllHotel();

    ResponseEntity<Hotel> getHotel(UUID id);

    void saveUsersFromExcel(MultipartFile file);

    ByteArrayInputStream exportToExcel();

    ResponseEntity<Hotel> getHotelWithRating(UUID id);
}
