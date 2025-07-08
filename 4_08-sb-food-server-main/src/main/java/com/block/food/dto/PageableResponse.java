package com.block.food.dto;

public class PageableResponse {
    public Integer page;
    public Integer size;
    public Integer totalElements;
    public Integer totalPages;

    public PageableResponse() {
    }

    public PageableResponse(Integer page, Integer size, Integer totalElements, Integer totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
