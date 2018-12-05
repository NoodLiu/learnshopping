package com.neuedu.service;


import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;

public interface UserService {


    ServerResponse login(String username,String password);

    ServerResponse register(UserInfo userInfo);

    ServerResponse QuestionByUserName(String username);

    ServerResponse QuestionToAnswer(String username,String question,String answer);
    /* 重置密码 */
    ServerResponse ResetPassword(String username,String newPassword,String Token);
}
