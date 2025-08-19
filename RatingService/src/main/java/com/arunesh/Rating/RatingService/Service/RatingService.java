package com.arunesh.Rating.RatingService.Service;

import com.arunesh.Rating.RatingService.Entity.CsvHeader;
import com.arunesh.Rating.RatingService.Entity.Rating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

public interface RatingService {
    ResponseEntity<List<Rating>> getAllRatings();

    ResponseEntity<Rating> getRatings(UUID id);

    ResponseEntity<String> saveRating(Rating rating);

    ResponseEntity<List<Rating>> getRatingByUserId(UUID id);

    ResponseEntity<List<Rating>> getRatingByHotelId(UUID id);

    void saveUsersFromCsv(MultipartFile file);

    ByteArrayInputStream exportAllUserDetailsToCsv(String[] header);
}
