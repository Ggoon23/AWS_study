package com.block.review.controller;

import com.block.review.dto.ProductDetailResponse;
import com.block.review.dto.ProductListResponse;
import com.block.review.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<ProductListResponse> getAllProducts(@RequestParam int page,
                                                              @RequestParam int size,
                                                              @RequestParam(required = false) String category) {

        ProductListResponse productListResponse = productService.getAllProducts(page, size, category);
        return ResponseEntity.status(200).body(productListResponse);

    }

    @GetMapping("/api/products/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable long productId){
        ProductDetailResponse productDetailResponse = productService.getProductById(productId);
        return ResponseEntity.status(200).body(productDetailResponse);
    }

}






