package com.arunesh.Rating.RatingService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    private UUID userId;
    private UUID hotelId;
    private int rate;
    private String feedback;
    @Transient
    private Hotel hotel;

}
