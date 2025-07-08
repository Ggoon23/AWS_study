package com.block.food.dto;

import java.util.List;

public class RestaurantDetailResponse {
    public Long id;
    public String name;
    public String category;
    public String address;
    public String phone;
    public String description;
    public Double avgRating;
    public Integer reviewCount;
    public String createdAt;
    public List<MenuResponse> menus;

    public RestaurantDetailResponse() {
    }

    public RestaurantDetailResponse(Long id, String name, String category, String address, String phone, String description, Double avgRating, Integer reviewCount, String createdAt, List<MenuResponse> menus) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.menus = menus;
    }
}
