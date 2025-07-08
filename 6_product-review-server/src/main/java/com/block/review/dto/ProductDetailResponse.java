package com.block.review.dto;

import java.util.List;

public class ProductDetailResponse {

    public ProductResponse product;
    public List<ReviewResponse> reviews;

    public ProductDetailResponse() {
    }

    public ProductDetailResponse(ProductResponse product, List<ReviewResponse> reviews) {
        this.product = product;
        this.reviews = reviews;
    }
}
