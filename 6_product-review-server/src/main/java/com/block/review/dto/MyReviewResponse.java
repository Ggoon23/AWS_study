package com.block.review.dto;

public class MyReviewResponse {
    public Long id;
    public Long productId;
    public String productName;
    public Integer rating;
    public String content;
    public String createdAt;
    public String updatedAt;

    public MyReviewResponse() {
    }

    public MyReviewResponse(Long id, Long productId, String productName, Integer rating, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
