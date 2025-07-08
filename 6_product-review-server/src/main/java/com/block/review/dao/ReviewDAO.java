package com.block.review.dao;

import com.block.review.dto.MyReviewResponse;
import com.block.review.dto.ReviewRequest;
import com.block.review.dto.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReviewDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<ReviewResponse> getReviewListByProductId(long productId){
        String sql = "SELECT r.id , r.user_id, u.nickname, r.rating, r.content, r.created_at\n" +
                "FROM reviews r\n" +
                "join users u\n" +
                "on r.user_id = u.id\n" +
                "where product_id =  ?  \n" +
                "order by r.created_at desc ;";
        return jdbcTemplate.query(sql, new ReviewRowMapper(), productId);
    }


    public int createReview(long productId, ReviewRequest reviewRequest){
        String sql = "INSERT INTO reviews (product_id, user_id, rating, content)\n" +
                "values ( ? , ?, ? , ? );";
        return jdbcTemplate.update(sql, productId, reviewRequest.userId,
                reviewRequest.rating, reviewRequest.content);
    }

    public int updateReview(long reviewId, ReviewRequest reviewRequest){
        String sql = "update reviews\n" +
                "set rating = ? , content = ? \n" +
                "where id = ? and user_id =  ? ;";
        return jdbcTemplate.update(sql, reviewRequest.rating,
                reviewRequest.content, reviewId, reviewRequest.userId);
    }


    public int deleteReview(long reviewId, long userId){
        String sql = "delete from reviews\n" +
                "where id = ? and user_id = ? ;";
        return jdbcTemplate.update(sql, reviewId, userId);
    }

    public List<MyReviewResponse> getReviewListByUserId(long userId, int offset, int size){
        String sql = "SELECT r.id, r.product_id, p.name as product_name, r.rating, r.content, r.created_at, r.updated_at\n" +
                "from reviews r \n" +
                "join products p \n" +
                "on r.product_id = p.id \n" +
                "where user_id =  ? \n" +
                "order by r.created_at desc\n" +
                "limit ? , ? ;";
        return jdbcTemplate.query(sql, new MyReviewRowMapper(), userId, offset, size);
    }


    public int getMyReviewTotalElements(long userId){
        String sql = "SELECT count(*)\n" +
                "from reviews\n" +
                "where user_id = ? ;";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }


    public static class MyReviewRowMapper implements RowMapper<MyReviewResponse>{

        @Override
        public MyReviewResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            MyReviewResponse myReviewResponse = new MyReviewResponse();
            myReviewResponse.id = rs.getLong("id");
            myReviewResponse.productId = rs.getLong("product_id");
            myReviewResponse.productName = rs.getString("product_name");
            myReviewResponse.rating = rs.getInt("rating");
            myReviewResponse.content = rs.getString("content");
            myReviewResponse.createdAt = rs.getString("created_at");
            myReviewResponse.updatedAt = rs.getString("updated_at");
            return myReviewResponse;
        }
    }


    public static class ReviewRowMapper implements RowMapper<ReviewResponse>{

        @Override
        public ReviewResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.id = rs.getLong("id");
            reviewResponse.userId = rs.getLong("user_id");
            reviewResponse.nickname = rs.getString("nickname");
            reviewResponse.rating = rs.getInt("rating");
            reviewResponse.content = rs.getString("content");
            reviewResponse.createdAt = rs.getString("created_at");
            return reviewResponse;
        }
    }

}



