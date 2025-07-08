package com.block.food.util;

import java.util.regex.Pattern;

public class NicknameValidator {

    public static boolean isValidInput(String input) {
        if (input == null) {
            return false;
        }
        
        String regex = "^[a-zA-Z0-9가-힣]{2,10}$";
        return Pattern.matches(regex, input);
    }
}
