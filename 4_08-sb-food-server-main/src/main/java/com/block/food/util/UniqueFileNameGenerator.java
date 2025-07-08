package com.block.food.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UniqueFileNameGenerator {
    public static String generateUniqueFileName(long userId, String extension) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return userId + "_" + timestamp + "_" + System.nanoTime() + extension;
    }
}
