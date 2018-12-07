package com.neuedu.controller.portal;


import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ServerResponse login(HttpSession httpSession,String username, String password) {
        ServerResponse serverResponse = userService.login(username, password);
        if (serverResponse.isSuccess()){
            /*登录成功.储存信息 */
            UserInfo userInfo = (UserInfo) serverResponse.getData();
            httpSession.setAttribute(Const.CURRENT_USER,userInfo);
        }
        return serverResponse;
    }

    /**
     * 获取用户信息
     * @param httpSession
     * @return
     */
    @GetMapping("/getuserinfo")
    public ServerResponse getuserinfo(HttpSession httpSession){
        Object o = httpSession.getAttribute(Const.CURRENT_USER);
        if (o!=null && o instanceof UserInfo){ //instanceof 判断类型
            UserInfo userInfo = (UserInfo) o;
            UserInfo result = new UserInfo();
            result.setId(userInfo.getId());
            result.setUsername(userInfo.getUsername());
            result.setEmail(userInfo.getEmail());
            result.setPhone(userInfo.getPhone());
            result.setCreateTime(userInfo.getCreateTime());
            result.setUpdateTime(userInfo.getUpdateTime());
            return ServerResponse.createServerResponseBySuccess(null,result);
        }
        return ServerResponse.createServerResponseByError(ResponseCode.USER_NOT_LOGIN.getStatus(),ResponseCode.USER_NOT_LOGIN.getMag());
    }

    @GetMapping("/getparticularuserinfo")
    public ServerResponse getparticularuserinfo(HttpSession httpSession){
        Object o = httpSession.getAttribute(Const.CURRENT_USER);
        if (o!=null && o instanceof UserInfo){ //instanceof 判断类型
            UserInfo userInfo = (UserInfo) o;
            return ServerResponse.createServerResponseBySuccess(null,userInfo);
        }
        return ServerResponse.createServerResponseByError(ResponseCode.USER_NOT_LOGIN.getStatus(),ResponseCode.USER_NOT_LOGIN.getMag());
    }


    /**
     * 添加（注册)
     *
     * @param
     */
    @GetMapping("/register")
    public ServerResponse register(HttpSession httpSession, UserInfo userInfo) {

        return userService.register(userInfo);
    }


    /**
     * 根据用户名查询密保问题-->忘记密码
     */
    @GetMapping("/usernamebyquestion")
    public ServerResponse UsernameByQuestion(String username) {
        return userService.questionByUserName(username);
    }

    /**
     * 提交问题答案
     *
     * @param
     */
    @GetMapping("/questiontoanswer")
    public ServerResponse QuestionToAnswer(String username, String question, String answer) {
        ServerResponse serverResponse = userService.questionToAnswer(username,question,answer);
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
    ServerResponse serverResponse = userService.resetPassword(username,newpassword,Token);
    return serverResponse;
    }

    /**
     * 登出
     */
    @GetMapping("/lagout.do")
    public ServerResponse lagout(HttpSession httpSession){
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createServerResponseBySuccess("退出成功");
    }

    /**
     * 登录状态下修改密码
     */
    @GetMapping("/reset_password.do")
    public ServerResponse ResetPasswordlogin(HttpSession httpSession,String newpassword,String oldpassword){
        Object o = httpSession.getAttribute(Const.CURRENT_USER);
        /* 获取登录信息 */
        System.out.println(o);
        if (o!=null && o instanceof UserInfo){
            /* 有登录信息 */
        UserInfo userInfo = (UserInfo) o;
       return userService.ResetPasswordlogin(userInfo,newpassword,oldpassword);
        }
        return ServerResponse.createServerResponseByError(ResponseCode.USER_NOT_LOGIN.getStatus(),ResponseCode.USER_NOT_LOGIN.getMag());
    }
}
