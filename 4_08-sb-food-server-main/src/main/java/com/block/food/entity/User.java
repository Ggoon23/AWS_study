package com.block.food.entity;

public class User {
    public Long id;
    public String email;
    public String password;
    public String nickname;
    public String role;
    public String createdAt;

    public User() {
    }


    public User(Long id, String email, String password, String nickname, String role, String createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.createdAt = createdAt;
    }
}
