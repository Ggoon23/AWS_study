package com.block.food.dto;

public class ReviewRequest {

    public Long restaurantId;
    public Long menuId;
    public Integer rating;
    public String content;

    public ReviewRequest() {
    }

    public ReviewRequest(Long restaurantId, Long menuId, Integer rating, String content) {
        this.restaurantId = restaurantId;
        this.menuId = menuId;
        this.rating = rating;
        this.content = content;
    }
}
