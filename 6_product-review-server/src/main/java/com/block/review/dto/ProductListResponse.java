package com.block.review.dto;

import org.springframework.data.relational.core.sql.In;

import java.util.List;

public class ProductListResponse {
    public List<ProductResponse> content;
    public Integer totalPages;
    public Integer totalElements;
    public Integer currentPage;
    public Integer pageSize;

    public ProductListResponse() {
    }

    public ProductListResponse(List<ProductResponse> content, Integer totalPages, Integer totalElements, Integer currentPage, Integer pageSize) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
