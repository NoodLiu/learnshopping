package com.neuedu.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderIdUtils {

    public static synchronized long generateUniqueKey(){
        Random random = new Random();
        int number = random.nextInt(899) + 100;
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddHHmm");//日期格式化
        String format = localDateTime.format(formatter);
        Integer orderId = Integer.parseInt(format+number);
        return orderId;
    }

}
