package com.block.review.dto;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public Integer price;
    public String category;
    public Integer stockQuantity;
    public Double averageRating;
    public Integer reviewCount;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, Integer price, String category, Integer stockQuantity, Double averageRating, Integer reviewCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }
}
