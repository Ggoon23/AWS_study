package com.block.review.eneity;

public class Product {
    public Long id;
    public String name;
    public String description;
    public Integer price;
    public String category;
    public Integer stockQuantity;
    public String createdAt;

    public Product() {
    }

    public Product(Long id, String name, String description, Integer price, String category, Integer stockQuantity, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
    }

}
