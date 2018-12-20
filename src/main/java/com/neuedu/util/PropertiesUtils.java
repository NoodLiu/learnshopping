package com.neuedu.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    private static Properties properties = new Properties();
    static {
        /* 读取文件流 */
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 读取配置文件中的内容
     */
    public static String readKey(String key){
        return  properties.getProperty(key);
    }
}
