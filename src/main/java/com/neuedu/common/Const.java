package com.neuedu.common;

public class Const {
   public static final Integer SUCCESS_CODE = 0;
   public static final Integer ERROR_CODE = 1;
   public static final String CURRENT_USER = "currentuser";
   public static final String TRADE_SUCCESS="TRADE_SUCCESS";
   public static final String TOKENKEY="tokenKey";


   public enum ReponseCodeEnum{

      NEDD_LOGIN(2,"需要登录"),
      NOT_POWER(3,"无权限");
      private int code;
      private String msg;

      ReponseCodeEnum(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }

      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }




   public enum  RoleEnumn{
      ROLE_ROOT(0,"管理员"),
      ROLE_CUSTOMER(1,"普通用户");
      private int code;
      private String msg;

      RoleEnumn(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }

      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }

   public enum Checked{
      ISCHECK(1,"选中"),
      NOCHECK(2,"未选中");

      private int code;
      private String msg;

      Checked(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }

      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }
   public enum OrderStaEnum{
     /* 0-已取消 10-未付款 20-已付款 30-已发货 40-交易成功 50-交易关闭*/
      ORDER_CANCELED(0,"已取消"),
      ORDER_UN_PAY(10,"未付款"),
      ORDER_PAYED(20,"已付款"),
      ORDER_SEND(30,"已发货"),
      ORDER_SUCCESS(40,"交易成功"),
      ORDER_CLOSED(50,"交易关闭");

      private int code;
      private String msg;

      OrderStaEnum(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }
      public static OrderStaEnum getmsg(Integer code){
         for (OrderStaEnum orderStaEnum : OrderStaEnum.values()) {
            if (code==orderStaEnum.code){
               return orderStaEnum;
            }
         }
         return null;
      }

      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }
   public enum PayType{
      ON_LINE(1,"线上支付");
      private int code;
      private String msg;

      PayType(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }
      public static PayType getmsg(Integer code){
         for (PayType PayStaEnum : PayType.values()) {
            if (code==PayStaEnum.code){
               return PayStaEnum;
            }
         }
         return null;
      }
      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }
   public enum PayPlatType{
      ALIPAY(1,"支付宝");
      private int code;
      private String msg;

      PayPlatType(int code, String msg) {
         this.code = code;
         this.msg = msg;
      }
      public static PayType getmsg(Integer code){
         for (PayType PayStaEnum : PayType.values()) {
            if (code==PayStaEnum.code){
               return PayStaEnum;
            }
         }
         return null;
      }
      public int getCode() {
         return code;
      }

      public void setCode(int code) {
         this.code = code;
      }

      public String getMsg() {
         return msg;
      }

      public void setMsg(String msg) {
         this.msg = msg;
      }
   }
}
