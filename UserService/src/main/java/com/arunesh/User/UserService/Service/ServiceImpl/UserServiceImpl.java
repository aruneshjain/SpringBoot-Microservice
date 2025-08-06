package com.arunesh.User.UserService.Service.ServiceImpl;

import com.arunesh.User.UserService.Entity.Rating;
import com.arunesh.User.UserService.Entity.Users;
import com.arunesh.User.UserService.Exception.ResourceNotFoundException;
import com.arunesh.User.UserService.Exception.UserNotFoundException;
import com.arunesh.User.UserService.Repository.UserRepository;
import com.arunesh.User.UserService.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<String> saveUser(Users user) {
        try{
            userRepository.save(user);
        }catch (Exception ex){
            return new ResponseEntity<>("User not saved : " + ex.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User Saved",
                HttpStatus.CREATED);
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
        try {
            ArrayList<Rating> rating = restTemplate.getForObject(
                    "http://RATING-SERVICE/rating/user/" + user.getId(),
                    ArrayList.class);
            logger.info("{}", rating);
            user.setRating(rating);
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (RuntimeException ex){
            throw new ResourceNotFoundException("Something went wrong in call Rating Service : " + ex);
        }
    }
}
