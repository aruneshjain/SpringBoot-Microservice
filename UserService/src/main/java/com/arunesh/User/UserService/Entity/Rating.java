package com.arunesh.User.UserService.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    public UUID id;
    private UUID userId;
    private UUID hotelId;
    private int rate;
    private String feedback;
}
