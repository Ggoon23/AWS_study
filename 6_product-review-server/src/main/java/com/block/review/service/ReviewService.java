package com.block.review.service;

import com.block.review.dao.ReviewDAO;
import com.block.review.dto.MyReviewListResponse;
import com.block.review.dto.MyReviewResponse;
import com.block.review.dto.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewDAO reviewDAO;

    public int createReview(long productId, ReviewRequest reviewRequest) {
        if( productId <= 0){
            return 1;
        }
        if( reviewRequest.userId <= 0) {
            return 2;
        }
        if( reviewRequest.rating < 1 || reviewRequest.rating > 5) {
            return 3;
        }
        if( reviewRequest.content == null || reviewRequest.content.length() < 10){
            return 4;
        }

        //  리뷰를 db 에 저장한다.
        try {
            reviewDAO.createReview(productId, reviewRequest);
        } catch (Exception e) {
            // 동일한 제품에 동일한 사람이 리뷰를 또 남기려고 하면
            // DB 에러가 발생한다.
            return 5;
        }

        return 0;
    }

    public int updateReview(long reviewId, ReviewRequest reviewRequest){

        // 평점은 1~5 사이의 값이어야 한다.
        if( reviewRequest.rating < 1 || reviewRequest.rating > 5) {
            return 1;
        }

        // 내용은 10자 이상이어야 한다.
        if( reviewRequest.content == null || reviewRequest.content.length() < 10){
            return 2;
        }

        // db에 저장한다.
        int result = reviewDAO.updateReview(reviewId, reviewRequest);

        if(result == 0){
            // 리뷰아이디가 없거나 해당 유저의 리뷰가 아닌경우.
            return 3;
        }

        return 0;
    }

    public int deleteReview(long reviewId, long userId){

        int result = reviewDAO.deleteReview(reviewId, userId);
        if (result == 1){
            return 0;
        }else{
            return 1;
        }
    }

    public MyReviewListResponse getReviewListByUserId(long userId, int page, int size){

        int offset = (page - 1) * size;

        List<MyReviewResponse> myReviewList = reviewDAO.getReviewListByUserId(userId, offset, size);

        int totalElements = reviewDAO.getMyReviewTotalElements(userId);

        // totalPages
        int totalPages = (int) Math.ceil( totalElements / (double)size );

        int currentPage = page;
        int pageSize = size;

        // json 으로 처리할 클래스에 데이터를 담는다.
        MyReviewListResponse myReviewListResponse = new MyReviewListResponse(myReviewList, totalPages, totalElements, currentPage, pageSize);

        return myReviewListResponse;

    }

}





