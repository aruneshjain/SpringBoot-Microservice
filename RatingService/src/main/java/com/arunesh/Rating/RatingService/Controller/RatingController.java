package com.arunesh.Rating.RatingService.Controller;

import com.arunesh.Rating.RatingService.Entity.Rating;
import com.arunesh.Rating.RatingService.Service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    RatingService ratingService;

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
}
