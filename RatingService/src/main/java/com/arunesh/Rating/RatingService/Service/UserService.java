package com.arunesh.Rating.RatingService.Service;

import com.arunesh.Rating.RatingService.Entity.Users;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "USER-SERVICE")
public interface UserService {

    @PostMapping("/user")
    public ResponseEntity<Users> saveUser(@RequestBody Users user);

    @GetMapping("/user/{id}")
    public Users getUser(@PathVariable UUID id);
}
