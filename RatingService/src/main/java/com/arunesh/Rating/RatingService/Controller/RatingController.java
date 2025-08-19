package com.arunesh.Rating.RatingService.Controller;

import com.arunesh.Rating.RatingService.Entity.CsvHeader;
import com.arunesh.Rating.RatingService.Entity.Rating;
import com.arunesh.Rating.RatingService.Exception.CSVFileException;
import com.arunesh.Rating.RatingService.Service.RatingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            ratingService.saveUsersFromCsv(file);
            return ResponseEntity.ok("CSV upload successful. Users saved.");
        } catch (RuntimeException e) {
            throw new CSVFileException(e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportAllUserDetailsToCsv(@RequestParam("type") String csvType){
        String filename = "AllUsers.csv";
        CsvHeader header;
        if(csvType.equalsIgnoreCase("full"))
            header = CsvHeader.CSV_FULL_DETAIL;
        else if (csvType.equalsIgnoreCase("rating"))
            header = CsvHeader.CSV_RATING_DETAIL;
        else
            header = CsvHeader.CSV_FEEDBACK_DETAIL;
        ByteArrayInputStream stream = ratingService.exportAllUserDetailsToCsv(header.getHeader().split(","));
        InputStreamResource file = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
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
