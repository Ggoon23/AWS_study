package com.block.food.controller;

import com.block.food.dto.ReviewRequest;
import com.block.food.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
public class ReviewController {

    @Autowired
    ReviewService reviewService;


    @PostMapping(value = "/api/v1/reviews/restaurant/{restaurantId}/menu/{menuId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Void> createReviewPhoto(
                         @RequestHeader("Authorization") String token,
                         @PathVariable long restaurantId,
                         @PathVariable long menuId,
                         @RequestParam("rating") String rating,
                         @RequestParam("content") String content,
                         @RequestParam(value = "image", required = false) MultipartFile image){

        try {
            reviewService.createReviewPhoto(token, restaurantId, menuId,
                    rating, content, image);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.status(201).build();
    }



    @PostMapping(value = "/api/v1/reviews", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Void> createReview(@RequestHeader("Authorization") String token,
                                        @RequestBody ReviewRequest reviewRequest){
        int result = reviewService.createReview(token, reviewRequest);
        if (result != 1 ){
            // result 가 1이라는 얘기는, 테이블에 행 하나 추가가 되었다는 뜻
            // 따라서 1이 아니라는 것은, 문제가 있는 경우다.
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

}
