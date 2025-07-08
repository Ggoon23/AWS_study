package com.block.food.service;

import com.block.food.config.JwtConfig;
import com.block.food.dao.UserDAO;
import com.block.food.dto.UserRequest;
import com.block.food.entity.User;
import com.block.food.util.EmailValidator;
import com.block.food.util.NicknameValidator;
import com.block.food.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtConfig jwtConfig;

    @Autowired
    UserDAO userDAO;

    public int signUp(UserRequest userRequest){

        if( EmailValidator.isValidEmail( userRequest.email ) == false ){
            return 1;
        }
        if( PasswordValidator.isValidPassword( userRequest.password ) == false ){
            return 2;
        }
        if(NicknameValidator.isValidInput(userRequest.nickname) == false){
            return 3;
        }

        System.out.println("UserService  1");

        // 비밀번호 암호화
        userRequest.password = passwordEncoder.encode(userRequest.password);

        // DB에 저장한다.
        try {
            userDAO.signUp(userRequest);
            System.out.println("UserService  2");
        } catch (Exception e) {
            // 이메일이 중복된경우.
            System.out.println("UserService  3");
            return 4;
        }
        return 0;
    }


    public Object userLogin(UserRequest userRequest) {

        // 이메일 형식이 올바른지 체크
        if( EmailValidator.isValidEmail( userRequest.email ) == false ){
            return 1;
        }

        try {
            User user = userDAO.userLogin(userRequest);

            // 비밀번호가 일치하는지 체크
            if( passwordEncoder.matches(userRequest.password, user.password) == false ){
                return 3;
            }

            // jwt 토큰 생성
            String token = jwtConfig.createToken(user.id);
            return token;

        } catch (Exception e) {
            return 2;
        }


    }

}




