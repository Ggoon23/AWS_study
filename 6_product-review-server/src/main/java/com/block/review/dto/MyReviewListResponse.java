package com.block.review.dto;

import java.util.List;

public class MyReviewListResponse {
    public List<MyReviewResponse> content;
    public Integer totalPages;
    public Integer totalElements;
    public Integer currentPage;
    public Integer pageSize;

    public MyReviewListResponse() {
    }

    public MyReviewListResponse(List<MyReviewResponse> content, Integer totalPages, Integer totalElements, Integer currentPage, Integer pageSize) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
