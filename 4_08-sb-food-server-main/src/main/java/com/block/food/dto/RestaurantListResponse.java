package com.block.food.dto;

import java.util.List;

public class RestaurantListResponse {

    public List<RestaurantResponse> content;

    public PageableResponse pageable;

    public RestaurantListResponse() {
    }

    public RestaurantListResponse(List<RestaurantResponse> content, PageableResponse pageable) {
        this.content = content;
        this.pageable = pageable;
    }

}
