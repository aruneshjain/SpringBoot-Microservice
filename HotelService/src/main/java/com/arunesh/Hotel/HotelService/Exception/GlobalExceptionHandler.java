package com.arunesh.Hotel.HotelService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HotelNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(HotelNotFoundException ex){
        ErrorResponse error = new ErrorResponse("Not Found", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

//    @ExceptionHandler({JwtInvalid.class})
//    public ResponseEntity<Object> handleJwtInvalid(JwtInvalid ex){
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ex.getMessage());
//    }
//
//    @ExceptionHandler(InvalidLoginException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidLogin(InvalidLoginException ex) {
//        ErrorResponse error = new ErrorResponse("Unauthorized", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(error);
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", HttpStatus.FORBIDDEN.value());
//        body.put("error", "Forbidden");
//        body.put("message", "You do not have permission to access this resource");
//        body.put("details", ex.getLocalizedMessage());
//
//        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
//    }

}
