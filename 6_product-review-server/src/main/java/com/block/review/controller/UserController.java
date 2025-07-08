package com.block.review.controller;

import com.block.review.dao.UserDAO;
import com.block.review.dto.UserRequest;
import com.block.review.dto.UserResponse;
import com.block.review.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest){

        int result = userService.createUser(userRequest);

        if(result == 0){
            return ResponseEntity.status(201).body(new UserResponse("success"));
        }else if(result == 1 || result == 2 || result == 3){
            return ResponseEntity.status(400).build();
        }else if (result == 4){
            return ResponseEntity.status(409).build();
        }else{
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserResponse> userLogin(@RequestBody UserRequest userRequest){
        int result = userService.userLogin(userRequest);
        if(result == 0){
            return ResponseEntity.status(200).body(new UserResponse("success"));
        }else if(result == 1 || result == 2){
            return ResponseEntity.status(400).build();
        }else if(result == 3) {
            return ResponseEntity.status(401).build();
        }else {
            return ResponseEntity.status(500).build();
        }
    }




}








