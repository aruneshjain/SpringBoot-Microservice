package com.arunesh.Hotel.HotelService.controller;

import com.arunesh.Hotel.HotelService.entity.Hotel;
import com.arunesh.Hotel.HotelService.exception.CustomFileimportException;
import com.arunesh.Hotel.HotelService.service.HotelService;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    HotelService hotelService;

    @PostMapping
    public ResponseEntity<Hotel> saveHotel(@RequestBody Hotel hotel){
        return hotelService.saveHotel(hotel);
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotel(){
        return hotelService.getAllHotel();
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable UUID id){
        return hotelService.getHotel(id);
    }
    @GetMapping(value = "/{id}/rating", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Hotel> getHotelWithRating(@PathVariable UUID id){
        return hotelService.getHotelWithRating(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            hotelService.saveUsersFromExcel(file);
            return ResponseEntity.ok("CSV upload successful. Hotels saved.");
        } catch (RuntimeException e) {
            throw new CustomFileimportException(e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportUsers() {
        String filename = "users.xlsx";
        ByteArrayInputStream stream = hotelService.exportToExcel();

        InputStreamResource file = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
