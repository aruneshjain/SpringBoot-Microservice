package com.arunesh.User.UserService.Service;

import com.arunesh.User.UserService.Entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    ResponseEntity<Users> saveUser(Users user);

    ResponseEntity<List<Users>> getAllUser();

    ResponseEntity<Users> getUser(UUID id);

    void saveUsersFromCsv(MultipartFile file);

    ByteArrayInputStream exportToCsv();

    ByteArrayInputStream exportUsersToPdf();

    ResponseEntity<String> bulkUpdateByUserId(String status, List<UUID> ids);

    ResponseEntity<Users> getUserWithRatings(UUID id);
}
