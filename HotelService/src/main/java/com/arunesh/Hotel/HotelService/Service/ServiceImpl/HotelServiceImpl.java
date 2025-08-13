package com.arunesh.Hotel.HotelService.Service.ServiceImpl;

import com.arunesh.Hotel.HotelService.Entity.Hotel;
import com.arunesh.Hotel.HotelService.Entity.Rating;
import com.arunesh.Hotel.HotelService.Exception.HotelNotFoundException;
import com.arunesh.Hotel.HotelService.Repository.HotelRepository;
import com.arunesh.Hotel.HotelService.Service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class HotelServiceImpl implements HotelService{

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private WebClient.Builder webClient;


    @Override
    public ResponseEntity<String> saveHotel(Hotel hotel) {
        try{
            hotelRepository.save(hotel);
        }catch (Exception ex){
            return new ResponseEntity<>("Hotel not saved : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Hotel Saved", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Hotel>> getAllHotel() {
        try {
            List<Hotel> hotel = hotelRepository.findAll();
            return new ResponseEntity<>(hotel,HttpStatus.OK);
        }catch (Exception ex){
            throw new HotelNotFoundException("Hotel not Available");
        }
    }

    @Override
    public ResponseEntity<Hotel> getHotel(UUID id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()-> new HotelNotFoundException("User Not Available with ID :" + id));
        try {
            List<Rating> rating = webClient.build()
                    .get()
                    .uri("http://RATING-SERVICE/rating/hotel/{id}", hotel.getId())
                    .retrieve()
                    .bodyToFlux(Rating.class)
                    .collectList()
                    .block();

            hotel.setRating(rating);
        }catch (Exception ex){
            System.out.println("ERROR : "+ ex);
        }
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }
}
