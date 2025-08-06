package com.arunesh.User.UserService.Service;

import com.arunesh.User.UserService.Entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    ResponseEntity<String> saveUser(Users user);

    ResponseEntity<List<Users>> getAllUser();

    ResponseEntity<Users> getUser(UUID id);
}
