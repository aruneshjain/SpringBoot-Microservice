package com.arunesh.User.UserService.Controller;

import com.arunesh.User.UserService.Entity.Rating;
import com.arunesh.User.UserService.Entity.Users;
import com.arunesh.User.UserService.Service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<String> saveUser(@RequestBody Users user){
        return userService.saveUser(user);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(){
        return userService.getAllUser();
    }

    @GetMapping("{id}")
    public ResponseEntity<Users> getUser(@PathVariable UUID id){
        return userService.getUser(id);
    }

}
