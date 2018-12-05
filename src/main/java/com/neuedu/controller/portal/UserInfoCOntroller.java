package com.neuedu.controller.portal;


import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * #### 一、用户模块
 * ###### 登录
 * ###### 注册
 * ###### 忘记密码
 * ###### 获取用户信息
 * ###### 修改密码
 * ###### 登出
 */
@RestController
@RequestMapping("/portal/user")
public class UserInfoCOntroller {

    @Autowired
    private UserService userService;

    /**
     * 登录
     *
     * @return
     */
    @GetMapping("/login")
    public ServerResponse login(String username, String password) {

        return userService.login(username, password);
    }

    /**
     * 添加（注册)
     *
     * @param
     */
    @GetMapping("/register")
    public ServerResponse save(HttpSession httpSession, UserInfo userInfo) {
        return userService.register(userInfo);
    }

    /**
     * 根据用户名查询密保问题-->忘记密码
     */
    @GetMapping("/usernamebyquestion")
    public ServerResponse UsernameByQuestion(String username) {
        return userService.QuestionByUserName(username);
    }

    /**
     * 提交问题答案
     *
     * @param
     */
    @GetMapping("/questiontoanswer")
    public ServerResponse QuestionToAnswer(String username, String question, String answer) {
        ServerResponse serverResponse = userService.QuestionToAnswer(username,question,answer);
        return serverResponse;
    }

    /**
     * 修改密码
     * @param username
     * @param newpassword
     * @param Token
     * @return
     */
    @GetMapping("/resetpassword")
    public ServerResponse ResetPassword(String username,String newpassword, String Token){
    ServerResponse serverResponse = userService.ResetPassword(username,newpassword,Token);
    return serverResponse;
    }

    /**
     * 登出
     */
    @GetMapping("/delete")
    public void delete(UserInfo userInfo) {

    }


}
