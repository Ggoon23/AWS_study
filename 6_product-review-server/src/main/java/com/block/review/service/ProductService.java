package com.block.review.service;

import com.block.review.dao.ProductDAO;
import com.block.review.dao.ReviewDAO;
import com.block.review.dto.ProductDetailResponse;
import com.block.review.dto.ProductListResponse;
import com.block.review.dto.ProductResponse;
import com.block.review.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    ReviewDAO reviewDAO;

    public ProductListResponse getAllProducts(int page, int size, String category){

        // 카테고리가 있는 경우와 없는 경우로 나눠서 처리한다.
        if(category == null || category.isEmpty() ){
            List<ProductResponse> productList = productDAO.getAllProducts(page, size);
            int totalElements = productDAO.getTotalElements();
            int totalPages = (int)Math.ceil(totalElements / (double)size);
            int currentPage = page;
            int pageSize = size;
            ProductListResponse productListResponse = new ProductListResponse(productList, totalPages, totalElements, currentPage, pageSize);
            return productListResponse;

        } else {

            List<ProductResponse> productList = productDAO.getAllProducts(page, size, category);
            int totalElements = productDAO.getTotalElements(category);
            int totalPages = (int)Math.ceil(totalElements / (double)size);
            int currentPage = page;
            int pageSize = size;
            ProductListResponse productListResponse = new ProductListResponse(productList, totalPages, totalElements, currentPage, pageSize);
            return productListResponse;
        }
    }

    public ProductDetailResponse getProductById(long productId) {
        ProductResponse product = productDAO.getProductById(productId);
        List<ReviewResponse> reviewList = reviewDAO.getReviewListByProductId(productId);
        ProductDetailResponse productDetailResponse = new ProductDetailResponse(product, reviewList);
        return productDetailResponse;
    }

}
