package com.arunesh.Rating.RatingService.Service.ServiceImpl;

import com.arunesh.Rating.RatingService.Entity.CsvHeader;
import com.arunesh.Rating.RatingService.Entity.Hotel;
import com.arunesh.Rating.RatingService.Entity.Rating;
import com.arunesh.Rating.RatingService.Entity.Users;
import com.arunesh.Rating.RatingService.Exception.CSVFileException;
import com.arunesh.Rating.RatingService.Exception.RatingNotFoundException;
import com.arunesh.Rating.RatingService.Repository.RatingRepository;
import com.arunesh.Rating.RatingService.Service.HotelService;
import com.arunesh.Rating.RatingService.Service.RatingService;
import com.arunesh.Rating.RatingService.Service.UserService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RatingServiceImpl implements RatingService {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    UserService userService;
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

    @Override
    public void saveUsersFromCsv(MultipartFile file) {
        try (
                CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
                Stream<String[]> lines = reader.readAll().stream()
        ) {
            // Skip the header and map remaining rows to User objects
            List<Rating> rating = lines
                    .skip(2) // skip header
                    .map(row -> {

                        if (row.length < 8) return null; // optional: skip malformed rows
                        Users user = userService.saveUser(Users.builder()
                                        .name(row[0])
                                        .email(row[1])
                                        .about(row[2])
                                        .build()).getBody();

                        Hotel hotel = hotelService.saveHotel(Hotel.builder()
                                .name(row[3])
                                .location(row[4])
                                .about(row[5])
                                .build()).getBody();


                        return Rating.builder()
                                .userId(user.getId())
                                .hotelId(hotel.getId())
                                .rate(Integer.parseInt(row[6]))
                                .feedback(row[7])
                                .build();
                    })
                    .filter(Objects::nonNull) // remove malformed rows
                    .toList();
            if(!rating.isEmpty())
                ratingRepository.saveAll(rating);
            else
                throw new CSVFileException("Empty CSV File");

        } catch (Exception e) {
            throw new CSVFileException("Failed to process CSV file: " + e.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream exportAllUserDetailsToCsv(String[] header) {
        List<Rating> ratings = ratingRepository.findAll();
        List<String> headerList = List.of(header);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
            // Header
            writer.writeNext(header);

            // Rows
            for (Rating rating : ratings ) {
                Hotel hotel = new Hotel();
                Users user = new Users();
                try {
                    user = userService.getUser(rating.getUserId());
                }catch (Exception ex){
                    System.out.println(ex + " --- Users Detail");
                }
                try {
                    hotel = hotelService.getHotel(rating.getHotelId());
                }catch (RuntimeException ex){
                    System.out.println(ex + " --- Hotel Details");
                }
                List<String> row = new ArrayList<>();

                if(headerList.contains("User-Name"))
                    row.add( user != null ? user.getName() : "");
                if(headerList.contains("User-Email"))
                    row.add(user != null ? user.getEmail() : "");

                if(headerList.contains("Hotel-Name"))
                    row.add(hotel != null ? hotel.getName() : "");
                if(headerList.contains("Hotel-Location"))
                    row.add(hotel != null ? hotel.getLocation() : "");

                if(headerList.contains("Rating-Rate"))
                    row.add(String.valueOf(rating.getRate()));
                if(headerList.contains("Rating-Feedback"))
                    row.add(rating.getFeedback());
                writer.writeNext(row.toArray(new String[0]));
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }


    public ResponseEntity<List<Rating>> rateHotelFallback(UUID id, Throwable ex){
        return new ResponseEntity<>(Collections.singletonList(
                Rating.builder()
                        .feedback("RateLimit : Too many Requests")
                        .build()),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
