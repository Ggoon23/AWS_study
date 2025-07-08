package com.block.review.dao;

import com.block.review.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<ProductResponse> getAllProducts(int page, int size){
        String sql = "SELECT p.id, p.name, p.description, p.price, p.category, p.stock_quantity, \n" +
                "\t\tIFNULL( avg(r.rating) , 0  )  as averageRating , \n" +
                "\t\tcount(r.id) as reviewCount\n" +
                "from products p\n" +
                "left join reviews r \n" +
                "on p.id = r.product_id \n" +
                "GROUP by p.id\n" +
                "order by p.id \n" +
                "limit ? , ? ;";
        int offset = (page - 1) * size;

        return jdbcTemplate.query(sql, new ProductRowMapper(), offset, size);

    }

    public List<ProductResponse> getAllProducts(int page, int size, String category){
        String sql = "SELECT p.id, p.name, p.description, p.price, p.category, p.stock_quantity ,\n" +
                "\t\t\tavg(r.rating) as averageRating , \n" +
                "\t\t\tcount( r.id ) as reviewCount\n" +
                "from products p\n" +
                "left join reviews r\n" +
                "on p.id = r.product_id\n" +
                "where category =  ?  \n" +
                "group by p.id\n" +
                "order by p.id\n" +
                "limit ? , ? ;";
        int offset = (page - 1) * size;
        return jdbcTemplate.query(sql, new ProductRowMapper(), category, offset, size);
    }

    public int getTotalElements(){
        String sql = "select count(*)\n" +
                "from products;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getTotalElements(String category){
        String sql = "select count(*)\n" +
                "from products\n" +
                "where category = ? ;";
        return jdbcTemplate.queryForObject(sql, Integer.class, category);
    }


    public ProductResponse getProductById(long productId){
        String sql = "select p.id, p.name, p.description, p.price, p.category,p.stock_quantity,\n" +
                "\t\tIFNULL( avg(r.rating) , 0   )  as averageRating ,  \n" +
                "\t\tcount(r.id) as reviewCount\n" +
                "from products p\n" +
                "left join reviews r \n" +
                "on p.id = r.product_id \n" +
                "where p.id = ? ;";
        return jdbcTemplate.queryForObject(sql, new ProductRowMapper(), productId);
    }



    public static class ProductRowMapper implements RowMapper<ProductResponse>{

        @Override
        public ProductResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductResponse productResponse = new ProductResponse();
            productResponse.id = rs.getLong("id");
            productResponse.name = rs.getString("name");
            productResponse.description = rs.getString("description");
            productResponse.price = rs.getInt("price");
            productResponse.category = rs.getString("category");
            productResponse.stockQuantity = rs.getInt("stock_quantity");
            productResponse.averageRating = rs.getDouble("averageRating");
            productResponse.reviewCount = rs.getInt("reviewCount");
            return productResponse;
        }
    }

}

