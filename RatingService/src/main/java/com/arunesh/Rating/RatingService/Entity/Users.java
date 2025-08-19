package com.arunesh.Rating.RatingService.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    private UUID id;

    private String name;
    private String email;
    private String about;
    private String isActive;
}
