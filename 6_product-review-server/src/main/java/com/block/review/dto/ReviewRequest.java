package com.block.review.dto;

public class ReviewRequest {
    public Long userId;
    public int rating;
    public String content;

    public ReviewRequest() {
    }

    public ReviewRequest(Long userId, int rating, String content) {
        this.userId = userId;
        this.rating = rating;
        this.content = content;
    }

}
