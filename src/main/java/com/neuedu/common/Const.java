package com.neuedu.common;

public class Const {
   public static final Integer SUCCESS_CODE = 0;
   public static final Integer ERROR_CODE = 1;

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
