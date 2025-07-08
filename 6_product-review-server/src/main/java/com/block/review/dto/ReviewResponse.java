package com.block.review.dto;

public class ReviewResponse {

    public Long id;
    public Long userId;
    public String nickname;
    public Integer rating;
    public String content;
    public String createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long userId, String nickname, Integer rating, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }
}
