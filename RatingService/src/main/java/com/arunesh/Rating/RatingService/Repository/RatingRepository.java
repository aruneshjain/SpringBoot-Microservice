package com.arunesh.Rating.RatingService.Repository;

import com.arunesh.Rating.RatingService.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findAllByUserId(UUID id);
}
