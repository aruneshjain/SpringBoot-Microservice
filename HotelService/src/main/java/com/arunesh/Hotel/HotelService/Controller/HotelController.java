package com.arunesh.Hotel.HotelService.Controller;

import com.arunesh.Hotel.HotelService.Entity.Hotel;
import com.arunesh.Hotel.HotelService.Service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    HotelService hotelService;

    @PostMapping
    public ResponseEntity<String> saveHotel(@RequestBody Hotel hotel){
        return hotelService.saveHotel(hotel);
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotel(){
        return hotelService.getAllHotel();
    }

    @GetMapping("{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable UUID id){
        return hotelService.getHotel(id);
    }
}
