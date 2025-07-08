package com.block.food.dao;

import com.block.food.dto.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int createReviewPhoto(long userId, long restaurantId,
            long menuId, String strRating, String content, String imageUrl){
        int rating = Integer.parseInt(strRating);
        String sql = "insert INTO review (user_id, restaurant_id, menu_id, rating, content, image_url)\n" +
                "values ( ? , ? , ? , ? , ? , ? );";
        return jdbcTemplate.update(sql, userId, restaurantId, menuId, rating, content, imageUrl);

    }

    public int createReviewPhoto(long userId, long restaurantId, long menuId,
                    String strRating, String content){
        int rating = Integer.parseInt(strRating);
        String sql = "insert INTO review (user_id, restaurant_id, menu_id, rating, content)\n" +
                "values (? , ? , ? , ? , ? );";
        return jdbcTemplate.update(sql, userId, restaurantId, menuId, rating, content);
    }



    public int createReview(long userId, ReviewRequest reviewRequest){
        String sql = "insert INTO review (user_id, restaurant_id, menu_id, rating, content)\n" +
                "values ( ? , ? , ? , ?, ? );";
        return jdbcTemplate.update(sql,
                userId, reviewRequest.restaurantId,
                reviewRequest.menuId, reviewRequest.rating,
                reviewRequest.content);
    }

}
