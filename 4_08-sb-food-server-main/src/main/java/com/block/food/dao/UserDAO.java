package com.block.food.dao;

import com.block.food.dto.UserRequest;
import com.block.food.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int signUp(UserRequest userRequest){
        // DB에 유저 정보를 저장하는 메소드
        String sql = "insert INTO user (email, password, nickname, role)\n" +
                "  values (? , ? , ? , 'USER');";
        return jdbcTemplate.update(sql, userRequest.email, userRequest.password, userRequest.nickname);
    }

    public User userLogin(UserRequest userRequest) {
        // DB에서 유저 정보를 조회하는 메소드
        String sql = "SELECT *\n" +
                " from user\n" +
                " where email = ? ;";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), userRequest.email);
    }

    public static class UserRowMapper implements RowMapper<User>{

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.id = rs.getLong("id");
            user.email = rs.getString("email");
            user.password = rs.getString("password");
            user.nickname = rs.getString("nickname");
            user.role = rs.getString("role");
            user.createdAt = rs.getString("created_at");
            return user;
        }
    }

}
