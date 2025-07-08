package com.block.review.eneity;

public class Review {
    public Long id;
    public Long productId;
    public Long userId;
    public Integer rating;
    public String content;
    public String createdAt;
    public String updatedAt;

    public Review() {
    }

    public Review(Long id, Long productId, Long userId, Integer rating, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
