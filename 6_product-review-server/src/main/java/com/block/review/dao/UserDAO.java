package com.block.review.dao;

import com.block.review.dto.UserRequest;
import com.block.review.eneity.User;
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

    public int createUser(UserRequest userRequest){
        String sql = "insert into users (email, password, nickname)\n" +
                "values ( ? , ? , ? );";
        return jdbcTemplate.update(sql, userRequest.email, userRequest.password, userRequest.nickname);
    }

    public User userLogin(UserRequest userRequest){
        String sql = "SELECT *\n" +
                "from users\n" +
                "WHERE  email = ? and password = ?  ; ";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), userRequest.email, userRequest.password);
    }

    public static class UserRowMapper implements RowMapper<User>{

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.id = rs.getLong("id");
            user.email = rs.getString("email");
            user.password = rs.getString("password");
            user.nickname = rs.getString("nickname");
            user.createdAt = rs.getString("created_at");
            return user;
        }
    }

}
