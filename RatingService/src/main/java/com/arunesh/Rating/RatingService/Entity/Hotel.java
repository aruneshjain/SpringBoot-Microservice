package com.arunesh.Rating.RatingService.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    private UUID id;

    private String name;
    private String location;
    private String about;
}
