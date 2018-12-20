package com.neuedu.util;


import java.math.BigDecimal;

/* 计算价格*/
public class BigdecimalUtils {


    /* 加  */
    public static BigDecimal add(Double a,Double b){
        BigDecimal bigDecimal= new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal2= new BigDecimal(String.valueOf(b));
        return bigDecimal.add(bigDecimal2);
    }
    /* 减 */
    public static BigDecimal sub(Double a,Double b){
        BigDecimal bigDecimal= new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal2= new BigDecimal(String.valueOf(b));
        return bigDecimal.subtract(bigDecimal2);
    }
    /* 乘 */
    public static BigDecimal mul(Double a,Double b){
        BigDecimal bigDecimal= new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal2= new BigDecimal(String.valueOf(b));
        return bigDecimal.multiply(bigDecimal2);
    }
    /* 除 */
    public static BigDecimal div(Double a,Double b){
        BigDecimal bigDecimal= new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal2= new BigDecimal(String.valueOf(b));
        return bigDecimal.divide(bigDecimal2);
    }
}
