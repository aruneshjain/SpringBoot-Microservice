package com.arunesh.Rating.RatingService.Service;

import com.arunesh.Rating.RatingService.Entity.Hotel;
import jakarta.ws.rs.Path;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelService {
    @GetMapping("/hotel/{id}")
    Hotel getHotel(@PathVariable UUID id);
}
