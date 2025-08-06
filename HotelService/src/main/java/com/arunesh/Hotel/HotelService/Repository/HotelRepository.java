package com.arunesh.Hotel.HotelService.Repository;

import com.arunesh.Hotel.HotelService.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}
