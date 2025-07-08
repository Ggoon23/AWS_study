package com.block.review.controller;

import com.block.review.dto.MyReviewListResponse;
import com.block.review.dto.ReviewRequest;
import com.block.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<Object> createReview(@PathVariable long productId,
                                        @RequestBody ReviewRequest reviewRequest) {

        int result = reviewService.createReview(productId, reviewRequest);

        if(result == 1 || result == 2){
            return ResponseEntity.status(404).build();
        } else if(result == 3 || result == 4 || result == 5){
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();

    }
    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Object> updateReview(@PathVariable long reviewId,
                                        @RequestBody ReviewRequest reviewRequest){

        int result = reviewService.updateReview(reviewId, reviewRequest);

        if(result == 1 || result == 2){
            return ResponseEntity.status(400).build();
        }else if(result == 3){
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(200).build();
    }


    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable long reviewId,
                                        @RequestParam long userId){

        int result = reviewService.deleteReview(reviewId, userId);

        if(result == 0){
            return ResponseEntity.status(204).build();
        }else {
            return ResponseEntity.status(404).build();
        }

    }

    @GetMapping("/api/users/{userId}/reviews")
    public ResponseEntity<MyReviewListResponse> getReviewListByUserId(@PathVariable long userId,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size){

        MyReviewListResponse myReviewListResponse = reviewService.getReviewListByUserId(userId, page, size);
        return ResponseEntity.status(200).body(myReviewListResponse);
    }

}





