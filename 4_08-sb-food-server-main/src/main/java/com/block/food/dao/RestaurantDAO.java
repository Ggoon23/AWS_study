package com.block.food.dao;

import com.block.food.dto.MenuResponse;
import com.block.food.dto.RestaurantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RestaurantDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<RestaurantResponse> getRestaurants(int offset, int size) {
        String sql = "SELECT r.id, r.name, r.category, r.address, r.phone, r.description, \n" +
                "\t\t\tIFNULL(  avg(rv.rating) , 0)  avg_rating   ,\n" +
                "\t\t\tcount(rv.id)  review_count,\n" +
                "\t\t\tr.created_at\n" +
                "from restaurant r\n" +
                "left join review  rv\n" +
                "on r.id = rv.restaurant_id\n" +
                "group by r.id \n" +
                "order by r.created_at desc\n" +
                "limit ? ,  ?  ;";
        return jdbcTemplate.query(sql, new RestaurantRowMapper(), offset, size);
    }


    public int getTotalElements(){
        String sql = "SELECT count(*)\n" +
                "from restaurant;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public List<RestaurantResponse> getRestaurants(int offset,
                                                   int size,
                                                   String category,
                                                   String keyword) {
        // 카테고리가 있고, 키워드가 없는 경우.
        if(category != null && keyword == null){
            String sql = "SELECT r.id, r.name, r.category, r.address, r.phone, r.description, \n" +
                    "\t\t\tIFNULL(  avg(rv.rating) , 0)  avg_rating   ,\n" +
                    "\t\t\tcount(rv.id)  review_count,\n" +
                    "\t\t\tr.created_at\n" +
                    "from restaurant r\n" +
                    "left join review  rv\n" +
                    "on r.id = rv.restaurant_id\n" +
                    "where r.category = ? \n" +
                    "group by r.id \n" +
                    "order by r.created_at desc\n" +
                    "limit ? , ? ;";
            return jdbcTemplate.query(sql, new RestaurantRowMapper(), category, offset, size);
        } else if(category == null && keyword != null){
            // 카테고리가 없고, 키워드가 있는 경우.
            String sql = "SELECT r.id, r.name, r.category, r.address, r.phone, r.description, \n" +
                    "\t\t\tIFNULL(  avg(rv.rating) , 0)  avg_rating   ,\n" +
                    "\t\t\tcount(rv.id)  review_count,\n" +
                    "\t\t\tr.created_at\n" +
                    "from restaurant r\n" +
                    "left join review  rv\n" +
                    "on r.id = rv.restaurant_id\n" +
                    "where r.name like ? or r.address like ? \n" +
                    "group by r.id \n" +
                    "order by r.created_at desc\n" +
                    "limit ? , ? ;";
            return jdbcTemplate.query(sql, new RestaurantRowMapper(), "%"+keyword+"%", "%"+keyword+"%" , offset, size);
        } else {
            // 카테고리와 키워드 둘다 있는 경우
            String sql = "SELECT r.id, r.name, r.category, r.address, r.phone, r.description, \n" +
                    "\t\t\tIFNULL(  avg(rv.rating) , 0)  avg_rating   ,\n" +
                    "\t\t\tcount(rv.id)  review_count,\n" +
                    "\t\t\tr.created_at\n" +
                    "from restaurant r\n" +
                    "left join review  rv\n" +
                    "on r.id = rv.restaurant_id\n" +
                    "where (r.name like ? or r.address like ? ) and category = ? \n" +
                    "group by r.id \n" +
                    "order by r.created_at desc\n" +
                    "limit ? ,  ? ;";
            return jdbcTemplate.query(sql, new RestaurantRowMapper(), "%"+keyword+"%", "%"+keyword+"%", category, offset, size);
        }







    }

    public int getTotalElements(String category, String keyword){

        if(category != null && keyword == null){
            // 카테고리는 있고, 키워드는 없는 경우
            String sql = "SELECT count(*)\n" +
                    "from restaurant\n" +
                    "where category = ? ;";
            return jdbcTemplate.queryForObject(sql, Integer.class, category);
        } else if (category == null && keyword != null){
            // 카테고리는 없고, 키워드만 있는 경우
            String sql = "SELECT count(*)\n" +
                    "from restaurant\n" +
                    "where name like ? or address like ?;";
            return jdbcTemplate.queryForObject(sql, Integer.class, "%"+keyword+"%", "%"+keyword+"%");
        } else {
            // 카테고리랑 키워드랑 둘다 있는 경우
            String sql = "SELECT count(*)\n" +
                    "from restaurant\n" +
                    "where (name like ? or address like ?) and category = ? ;";
            return jdbcTemplate.queryForObject(sql, Integer.class, "%"+keyword+"%", "%"+keyword+"%", category );
        }


    }


    public RestaurantResponse getRestaurantDetail(long id){
        String sql = "SELECT r.id, r.name, r.category, r.address, r.phone, r.description, \n" +
                "\t\t\tIFNULL(  avg(rv.rating) , 0)  avg_rating   ,\n" +
                "\t\t\tcount(rv.id)  review_count,\n" +
                "\t\t\tr.created_at\n" +
                "from restaurant r\n" +
                "left join review  rv\n" +
                "on r.id = rv.restaurant_id\n" +
                "where r.id = ? \n" +
                "group by r.id;";
        return jdbcTemplate.queryForObject(sql, new RestaurantRowMapper(), id );
    }


    public List<MenuResponse> getMenuList(long id){
        String sql = "SELECT m.id, m.name, m.price, m.description, m.category , \n" +
                "\t\t\tcount( r.id ) reviewCount\n" +
                "from menu m \n" +
                "left join review r \n" +
                "on m.id = r.menu_id\n" +
                "where m.restaurant_id =  ?  \n" +
                "group by m.id;";
        return jdbcTemplate.query(sql, new MenuRowMapper(), id );
    }

    public static class MenuRowMapper implements RowMapper<MenuResponse>{

        @Override
        public MenuResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            MenuResponse menuResponse = new MenuResponse();
            menuResponse.id = rs.getLong("id");
            menuResponse.name = rs.getString("name");
            menuResponse.price = rs.getInt("price");
            menuResponse.description = rs.getString("description");
            menuResponse.category = rs.getString("category");
            menuResponse.reviewCount = rs.getInt("reviewCount");
            return menuResponse;
        }
    }


    public static class RestaurantRowMapper implements RowMapper<RestaurantResponse> {

        @Override
        public RestaurantResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            RestaurantResponse restaurantResponse = new RestaurantResponse();
            restaurantResponse.id = rs.getLong("id");
            restaurantResponse.name = rs.getString("name");
            restaurantResponse.category = rs.getString("category");
            restaurantResponse.address = rs.getString("address");
            restaurantResponse.phone = rs.getString("phone");
            restaurantResponse.description = rs.getString("description");
            restaurantResponse.avgRating = rs.getDouble("avg_rating");
            restaurantResponse.reviewCount = rs.getInt("review_count");
            restaurantResponse.createdAt = rs.getString("created_at");
            return restaurantResponse;
        }
    }

}
