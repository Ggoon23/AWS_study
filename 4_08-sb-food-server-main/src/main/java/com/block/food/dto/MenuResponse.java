package com.block.food.dto;

public class MenuResponse {

    public Long id;
    public String name;
    public Integer price;
    public String description;
    public String category;
    public Integer reviewCount;

    public MenuResponse() {
    }

    public MenuResponse(Long id, String name, Integer price, String description, String category, Integer reviewCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.reviewCount = reviewCount;
    }
}
