package com.block.food.service;

import com.block.food.dao.RestaurantDAO;
import com.block.food.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    RestaurantDAO restaurantDAO;

    public RestaurantListResponse getRestaurants(int page, int size){
        // SQL에서 limit을 사용하여 페이징 처리를 해야함
        int offset = (page - 1) * size;

        List<RestaurantResponse> restaurantList = restaurantDAO.getRestaurants(offset, size);

        int totalElements = restaurantDAO.getTotalElements();
        int totalPages = (int) Math.ceil( (double) totalElements / size );

        PageableResponse pageableResponse = new PageableResponse(page, size, totalElements,totalPages);

        RestaurantListResponse restaurantListResponse =
                new RestaurantListResponse(restaurantList, pageableResponse);

        return restaurantListResponse;

    }

    public RestaurantListResponse getRestaurants(int page,
                                                 int size,
                                                 String category,
                                                 String keyword){
        // 카테고리가 있고 키워드는 없는 경우
        if(category != null && keyword == null){
            int offset = (page - 1) * size;
            List<RestaurantResponse> restaurantList = restaurantDAO.getRestaurants(offset, size, category, null);
            int totalElements = restaurantDAO.getTotalElements(category, null);
            int totalPages = (int) Math.ceil( (double) totalElements / size );

            PageableResponse pageableResponse =
                    new PageableResponse(page, size, totalElements,totalPages);
            RestaurantListResponse restaurantListResponse =
                    new RestaurantListResponse(restaurantList, pageableResponse);
            return restaurantListResponse;
        } else if(category == null && keyword != null){
            // 카테고리가 없고 키워드가 있는 경우
            int offset = (page - 1) * size;
            List<RestaurantResponse> restaurantList = restaurantDAO.getRestaurants(offset, size, null, keyword);
            int totalElements = restaurantDAO.getTotalElements(null, keyword);
            int totalPages = (int) Math.ceil( (double) totalElements / size );

            PageableResponse pageableResponse =
                    new PageableResponse(page, size, totalElements,totalPages);
            RestaurantListResponse restaurantListResponse =
                    new RestaurantListResponse(restaurantList, pageableResponse);
            return restaurantListResponse;
        } else {
            // 카테고리와 키워드가 모두 있는 경우
            int offset = (page - 1) * size;
            List<RestaurantResponse> restaurantList = restaurantDAO.getRestaurants(offset, size, category, keyword);
            int totalElements = restaurantDAO.getTotalElements(category, keyword);
            int totalPages = (int) Math.ceil( (double) totalElements / size );

            PageableResponse pageableResponse =
                    new PageableResponse(page, size, totalElements,totalPages);
            RestaurantListResponse restaurantListResponse =
                    new RestaurantListResponse(restaurantList, pageableResponse);
            return restaurantListResponse;
        }


    }
    public RestaurantDetailResponse getRestaurantDetail(long id){
        RestaurantResponse restaurantResponse =
                restaurantDAO.getRestaurantDetail(id);

        List<MenuResponse> menuList = restaurantDAO.getMenuList(id);

        RestaurantDetailResponse restaurantDetailResponse =
                new RestaurantDetailResponse();
        restaurantDetailResponse.id = restaurantResponse.id;
        restaurantDetailResponse.menus = menuList;
        restaurantDetailResponse.address = restaurantResponse.address;
        restaurantDetailResponse.createdAt = restaurantResponse.createdAt;
        restaurantDetailResponse.avgRating = restaurantResponse.avgRating;
        restaurantDetailResponse.category = restaurantResponse.category;
        restaurantDetailResponse.description = restaurantResponse.description;
        restaurantDetailResponse.name = restaurantResponse.name;
        restaurantDetailResponse.phone = restaurantResponse.phone;
        restaurantDetailResponse.reviewCount = restaurantResponse.reviewCount;

        return restaurantDetailResponse;

    }
}
