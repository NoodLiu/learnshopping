package com.neuedu.service;


import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;

import javax.servlet.http.HttpSession;

public interface UserService {


    ServerResponse login(String username,String password);

    ServerResponse register(UserInfo userInfo);

    ServerResponse questionByUserName(String username);

    ServerResponse questionToAnswer(String username,String question,String answer);
    /* 重置密码 */
    ServerResponse resetPassword(String username,String newPassword,String Token);

    ServerResponse ResetPasswordlogin(UserInfo userInfo, String newpassword, String oldpassword);
}
