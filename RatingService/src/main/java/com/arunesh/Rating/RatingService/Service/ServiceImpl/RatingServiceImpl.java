package com.arunesh.Rating.RatingService.Service.ServiceImpl;

import com.arunesh.Rating.RatingService.Entity.Hotel;
import com.arunesh.Rating.RatingService.Entity.Rating;
import com.arunesh.Rating.RatingService.Exception.RatingNotFoundException;
import com.arunesh.Rating.RatingService.Repository.RatingRepository;
import com.arunesh.Rating.RatingService.Service.HotelService;
import com.arunesh.Rating.RatingService.Service.RatingService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {
    @Autowired
    RatingRepository ratingRepository;
//    @Autowired
//    RestTemplate restTemplate;
    @Autowired
    HotelService hotelService;

    @Override
    public ResponseEntity<List<Rating>> getAllRatings() {
        try {
            List<Rating> rating = ratingRepository.findAll();
            return new ResponseEntity<>(rating,HttpStatus.OK);
        }catch (Exception ex){
            throw new RatingNotFoundException("Rating not Available");
        }
    }

    @Override
    public ResponseEntity<Rating> getRatings(UUID id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(()-> new RatingNotFoundException("Rating Not Available with ID :" + id));

        return new ResponseEntity<>(rating,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> saveRating(Rating rating) {
        try{
            ratingRepository.save(rating);
        }catch (Exception ex){
            return new ResponseEntity<>("Rating not saved : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Rating Saved", HttpStatus.CREATED);
    }

    @Override
    @RateLimiter(name = "rateHotelFallback", fallbackMethod = "rateHotelFallback")
    public ResponseEntity<List<Rating>> getRatingByUserId(UUID id) {
        List<Rating> rating;
        try {
            rating = ratingRepository.findAllByUserId(id);
        }catch (RuntimeException ex){
            throw new RatingNotFoundException("Rating not Available : " + ex.getMessage());
        }
        if(!rating.isEmpty()) {
            List<Rating> ratingWithHotel = rating.stream().map((R) -> {
                // Feign Client Call for Hotel Service
                Hotel hotel = hotelService.getHotel(R.getHotelId());
                R.setHotel(hotel);
                return R;
            }).toList();
            return new ResponseEntity<>(ratingWithHotel, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(rating,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Rating>> getRatingByHotelId(UUID id) {
        List<Rating> rating;
        try {
            rating = ratingRepository.findAllByHotelId(id);
        }catch (RuntimeException ex){
            throw new RatingNotFoundException("Rating not Available : " + ex.getMessage());
        }
        return new ResponseEntity<>(rating,HttpStatus.OK);
    }

    public ResponseEntity<List<Rating>> rateHotelFallback(UUID id, Throwable ex){
        return new ResponseEntity<>(Collections.singletonList(
                Rating.builder()
                        .feedback("RateLimit : Too many Requests")
                        .build()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
