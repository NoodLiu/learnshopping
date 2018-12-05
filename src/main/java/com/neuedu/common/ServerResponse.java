package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 封装给前端返回的高复用对象
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
    /* 状态码 */
    private int status;
    /* 状态信息 */
    private String msg;
    /* 成功后响应的数据 */
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ServerResponse() {
    }

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 判断接口是否调用成功
     * @return
     */
    @JsonIgnore
    public boolean isSuccess(){

        return  this.status==Const.SUCCESS_CODE;

    }

    /**
     * 成功
     */
    public static ServerResponse createServerResponseBySuccess() {
        return new ServerResponse(Const.SUCCESS_CODE);
    }

    public static ServerResponse createServerResponseBySuccess(String msg) {
        return new ServerResponse(Const.SUCCESS_CODE, msg);
    }

    public static <T> ServerResponse createServerResponseBySuccess(String msg, T data) {
        return new ServerResponse(Const.SUCCESS_CODE, msg, data);
    }

    /**
     * 失败
     */
    public static ServerResponse createServerResponseByError() {
        return new ServerResponse(Const.ERROR_CODE);
    }
    public static ServerResponse createServerResponseByError(String msg) {
        return new ServerResponse(Const.ERROR_CODE,msg);
    }
    public static ServerResponse createServerResponseByError(Integer status) {
        return new ServerResponse(Const.ERROR_CODE);
    }
    public static ServerResponse createServerResponseByError(Integer status,String msg) {
        return new ServerResponse(status,msg);
    }

}
