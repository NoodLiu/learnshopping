package com.neuedu.common;


/**
 * 状态码及信息
 */
public enum ResponseCode {
    ERROR(4,"错误"),
    PARAMETERS_REQUIRED(1,"参数必填"),
    USERNAME_EXTIS(2,"用户名已存在"),
    EMAIL_EXTIS(3,"邮箱已存在"),
    USER_NOT_LOGIN(5,"用户未登录");
    int status;
    String mag;

    ResponseCode() {
    }

    ResponseCode(int status, String mag) {
        this.status = status;
        this.mag = mag;
    }

    public int getStatus() {
        return status;
    }

    public String getMag() {
        return mag;
    }
}
