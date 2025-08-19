package com.arunesh.Hotel.HotelService.service.serviceImpl;

import com.arunesh.Hotel.HotelService.entity.Hotel;
import com.arunesh.Hotel.HotelService.entity.Rating;
import com.arunesh.Hotel.HotelService.exception.CustomFileimportException;
import com.arunesh.Hotel.HotelService.exception.HotelNotFoundException;
import com.arunesh.Hotel.HotelService.repository.HotelRepository;
import com.arunesh.Hotel.HotelService.service.HotelService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class HotelServiceImpl implements HotelService{

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private WebClient.Builder webClient;


    @Override
    public ResponseEntity<Hotel> saveHotel(Hotel hotel) {
        try{
            Hotel saveHotel = hotelRepository.save(hotel);
            return new ResponseEntity<>(saveHotel, HttpStatus.CREATED);
        }catch (Exception ex){
            return new ResponseEntity<>(Hotel.builder().build(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<Hotel>> getAllHotel() {
        try {
            List<Hotel> hotel = hotelRepository.findAll();
            return new ResponseEntity<>(hotel,HttpStatus.OK);
        }catch (Exception ex){
            throw new HotelNotFoundException("Hotel not Available");
        }
    }

    @Override
    public ResponseEntity<Hotel> getHotel(UUID id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()-> new HotelNotFoundException("User Not Available with ID :" + id));
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Hotel> getHotelWithRating(UUID id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()-> new HotelNotFoundException("User Not Available with ID :" + id));
        try {
            List<Rating> rating = webClient.build()
                    .get()
                    .uri("http://RATING-SERVICE/rating/hotel/{id}", hotel.getId())
                    .retrieve()
                    .bodyToFlux(Rating.class)
                    .collectList()
                    .block();

            hotel.setRating(rating);
        }catch (Exception ex){
            System.out.println("ERROR : "+ ex);
        }
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }

    @Override
    public void saveUsersFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(file.getContentType());
            List<Hotel> hotel = IntStream.rangeClosed(1, sheet.getLastRowNum()) // skip header row (0)
                    .mapToObj(sheet::getRow)
                    .filter(Objects::nonNull)
                    .map(row -> {
                        return Hotel.builder()
                                .name(row.getCell(0).getStringCellValue())
                                .location(row.getCell(1).getStringCellValue())
                                .about(row.getCell(2).getStringCellValue())
                                .build();
                    })
                    .collect(Collectors.toList());
            hotelRepository.saveAll(hotel);
        }catch (RuntimeException | IOException ex){
            throw new CustomFileimportException("Failed to process Excel file: " + ex.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream exportToExcel() {
        List<Hotel> hotels = hotelRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Location", "About"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Rows
            int rowIdx = 1;
            for (Hotel hotel : hotels) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(hotel.getId()));
                row.createCell(1).setCellValue(hotel.getName());
                row.createCell(2).setCellValue(hotel.getLocation());
                row.createCell(3).setCellValue(hotel.getAbout());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel: " + e.getMessage());
        }
    }
}
