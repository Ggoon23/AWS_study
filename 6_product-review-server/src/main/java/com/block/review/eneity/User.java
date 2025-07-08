package com.block.review.eneity;

public class User {

    public Long id;
    public String email;
    public String password;
    public String nickname;
    public String createdAt;

    public User() {
    }

    public User(Long id, String email, String password, String nickname, String createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
