package com.arunesh.Rating.RatingService.Controller;

import com.arunesh.Rating.RatingService.Entity.Rating;
import com.arunesh.Rating.RatingService.Service.RatingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    RatingService ratingService;
//    Logger logger = new Logger();

    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings(){
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatings(@PathVariable UUID id){
        return ratingService.getRatings(id);
    }

    @PostMapping
    public ResponseEntity<String> saveRating(@RequestBody Rating rating){
        return ratingService.saveRating(rating);
    }

    @GetMapping("/hotel/{id}")
    public ResponseEntity<List<Rating>> getRatingByHotelId(@PathVariable UUID id){
        return ratingService.getRatingByHotelId(id);
    }

    @GetMapping("/user/{id}")
//    @CircuitBreaker(name = "ratingHotelBreaker",fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name = "ratingRateLimiter", fallbackMethod = "ratingRateLimiter")
    public ResponseEntity<List<Rating>> getRatingByUserId(@PathVariable UUID id){
        return ratingService.getRatingByUserId(id);
    }
    //creating fallback method for circuitbreaker
    public ResponseEntity<List<Rating>> ratingRateLimiter(UUID id,Exception ex){
        List<Rating> rating = Collections.singletonList(Rating.builder()
                .rate(0)
                .hotel(null)
                .hotelId(null)
                .feedback("Fallback : " + ex)
                .id(null)
                .build());
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }
}
