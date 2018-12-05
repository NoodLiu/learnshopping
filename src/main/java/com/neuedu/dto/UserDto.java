package com.neuedu.dto;

import lombok.Data;


@Data
public class UserDto {
    /* 用户id */
    private Integer id;
    /*  用户名 */
    private String username;
    /* 用户密码 */
    private String password;
    /* 邮箱 */
    private String email;
    /* 手机号 */
    private String phone;
    /* 密保问题 */
    private String question;
    /* 答案 */
    private String answer;
    /* 角色 */
    private Integer role;

}
