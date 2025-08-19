package com.arunesh.User.UserService.Service.ServiceImpl;

import com.arunesh.User.UserService.Entity.Rating;
import com.arunesh.User.UserService.Entity.Users;
import com.arunesh.User.UserService.Exception.FileUploadException;
import com.arunesh.User.UserService.Exception.UserNotFoundException;
import com.arunesh.User.UserService.Repository.UserRepository;
import com.arunesh.User.UserService.Service.UserService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<Users> saveUser(Users user) {
        try{
            Users newUser = userRepository.save(user);
            return new ResponseEntity<>(newUser,HttpStatus.CREATED);
        }catch (Exception ex){
            return new ResponseEntity<>(Users.builder().build(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<Users>> getAllUser() {
        try {
            List<Users> user = userRepository.findAll();
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception ex){
            throw new UserNotFoundException("Users not Available");
        }
    }

    @Override
    public ResponseEntity<Users> getUser(UUID id) {
        Users user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Not Available with ID :" + id));
        return new ResponseEntity<>(user,HttpStatus.OK);
    }


    @Override
    @CircuitBreaker(name = "ratingHotelFallback", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<Users> getUserWithRatings(UUID id) {

        Users user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User Not Available with ID :" + id));
        try {
            ArrayList<Rating> rating = restTemplate.getForObject(
                    "http://RATING-SERVICE/rating/user/" + user.getId(),
                    ArrayList.class);
            logger.info("{}", rating);
            user.setRating(rating);
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception ex){
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> bulkUpdateByUserId(String status, List<UUID> ids) {
        int updatedRecords = userRepository.bulkUpdateIsActiveByIds(status,ids);
        return new ResponseEntity<>(updatedRecords + " Entries Updated out of : " + ids.size(),
                HttpStatus.OK);
    }

    @Override
    public void saveUsersFromCsv(MultipartFile file) {
        try (
                CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
                Stream<String[]> lines = reader.readAll().stream()
        ) {
            // Skip the header and map remaining rows to User objects
            List<Users> users = lines
                    .skip(1) // skip header
                    .map(row -> {
                        if (row.length < 3) return null; // optional: skip malformed rows
                        return Users.builder()
                                .name(row[0])
                                .email(row[1])
                                .about(row[2])
                                .build();
                    })
                    .filter(Objects::nonNull) // remove malformed rows
                    .collect(Collectors.toList());
            if(!users.isEmpty())
                userRepository.saveAll(users);
            else
                throw new FileUploadException("Empty CSV File");

        } catch (Exception e) {
            throw new FileUploadException("Failed to process CSV file: " + e.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream exportToCsv() {
        List<Users> users = userRepository.findAll();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            // Header
            writer.writeNext(new String[]{"ID", "Name", "Email", "About"});

            // Rows
            for (Users user : users) {
                writer.writeNext(new String[]{
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getEmail(),
                        user.getAbout()
                });
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream exportUsersToPdf() {
        List<Users> users = userRepository.findAll();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
            Font bodyFont = new Font(Font.TIMES_ROMAN, 12);

            document.add(new Paragraph("User List", headerFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{5, 3, 3, 3});

            Stream.of("ID", "Name", "Email", "About")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell();
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        cell.setPhrase(new Phrase(header, bodyFont));
                        table.addCell(cell);
                    });

            for (Users user : users) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getName());
                table.addCell(user.getEmail());
                table.addCell(user.getAbout());
            }

            document.add(table);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }


    public ResponseEntity<Users> ratingHotelFallback(UUID id, Exception ex) {
        Users user = Users.builder()
                .about("Rating service is down please try after some time.").build();
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
}
