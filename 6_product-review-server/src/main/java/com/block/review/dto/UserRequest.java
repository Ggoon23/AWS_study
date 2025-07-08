package com.block.review.dto;

public class UserRequest {

    public String email;
    public String password;
    public String nickname;

    public UserRequest() {
    }

    public UserRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

}
