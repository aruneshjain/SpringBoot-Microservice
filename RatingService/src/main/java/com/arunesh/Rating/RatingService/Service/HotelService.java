package com.arunesh.Rating.RatingService.Service;

import com.arunesh.Rating.RatingService.Entity.Hotel;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelService {

    @GetMapping("/hotel/{id}")
    @Retry(name = "retryHotelFallback", fallbackMethod = "retryHotelFallback")
//    @CircuitBreaker(name = "hotelFallback", fallbackMethod = "hotelFallback")
    Hotel getHotel(@PathVariable UUID id);

    default Hotel retryHotelFallback(UUID userId, Exception ex) {
        return Hotel.builder()
                .about("Fallback : Hotel Service is down please try after some time")
                .build();
    }

}
