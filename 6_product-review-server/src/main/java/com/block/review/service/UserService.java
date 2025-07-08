package com.block.review.service;

import com.block.review.dao.UserDAO;
import com.block.review.dto.UserRequest;
import com.block.review.eneity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public int createUser(UserRequest userRequest){

        //   이메일은 유효한 이메일 형식이어야 합니다.
        if (userRequest.email.contains("@") == false ){
            return 1;
        }
        //   비밀번호는 최소 8자 이상이어야 합니다.
        if (userRequest.password.length() < 8){
            return 2;
        }
        //   닉네임은 2자 이상 30자 이하여야 합니다.
        if (userRequest.nickname.length() < 2 || userRequest.nickname.length() > 30){
            return 3;
        }
        try{
            userDAO.createUser(userRequest);
            return 0;
        } catch (Exception e) {
            // 중복이메일인경우. : DB 유니크 에러난 경우.
            return 4;
        }
    }

    public int userLogin(UserRequest userRequest){
        if(userRequest.email == null || userRequest.email.isEmpty()){
            return 1;
        }
        if(userRequest.password == null || userRequest.password.isEmpty()){
            return 2;
        }

        // DB에서 유저 정보를 받아온다.
        try{
            userDAO.userLogin( userRequest );
            return 0;
        } catch (Exception e) {
            return 3;
        }



    }



}





