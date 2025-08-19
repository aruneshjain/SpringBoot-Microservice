package com.arunesh.User.UserService.Controller;

import com.arunesh.User.UserService.Entity.BulkUpdateRequest;
import com.arunesh.User.UserService.Entity.Users;
import com.arunesh.User.UserService.Exception.FileUploadException;
import com.arunesh.User.UserService.Service.UserService;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Users> saveUser(@RequestBody Users user){
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

    @GetMapping("{id}/rating")
    public ResponseEntity<Users> getUserWithRatings(@PathVariable UUID id){
        return userService.getUserWithRatings(id);
    }

    @PostMapping("bulk-update")
    public ResponseEntity<String> bulkUpdateByUserId(@RequestBody BulkUpdateRequest request){
        return userService.bulkUpdateByUserId(request.getStatus(),request.getIds());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
                userService.saveUsersFromCsv(file);
            return ResponseEntity.ok("File upload successful. Users saved.");
        } catch (RuntimeException e) {
            throw new FileUploadException(e.getMessage());
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportUsers() {
        String filename = "users.csv";
        ByteArrayInputStream stream = userService.exportToCsv();
        InputStreamResource file = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPdf() {
        ByteArrayInputStream pdfStream = userService.exportUsersToPdf();

        InputStreamResource resource = new InputStreamResource(pdfStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

}
