package com.neuedu.common;

public class Const {
   public static final Integer SUCCESS_CODE = 0;
   public static final Integer ERROR_CODE = 1;
   public static final String CURRENT_USER = "currentuser";


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
}
