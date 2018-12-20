package com.neuedu.common;

import lombok.Getter;

@Getter
public enum  ProductStatusEnum{
    /* 1：在架 2：下架 3：删除 */

    UP(1, "在架"),
    DOWN(2, "下架"),
    DELECT(3,"删除");

    private Integer code;
    private String  message;


    ProductStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
