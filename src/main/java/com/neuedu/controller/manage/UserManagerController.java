package com.neuedu.controller.manage;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserMangerService;
import com.neuedu.util.GetIpUtils;
import com.neuedu.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private UserMangerService userMangerService;

    /**
     * 管理员登录
     *
     * @return
     */
    @GetMapping("/login/{username}/{password}")
    public ServerResponse login(HttpServletRequest request, HttpServletResponse response,HttpSession httpSession, @PathVariable("username") String username, @PathVariable("password") String password) {
        ServerResponse serverResponse = userMangerService.login(username, password);
        UserInfo userInfo = (UserInfo) serverResponse.getData();
        System.out.println(userInfo.getRole());
        if (serverResponse.isSuccess()){

            if(userInfo.getRole()!=Const.RoleEnumn.ROLE_ROOT.getCode()){
                return ServerResponse.createServerResponseByError("无权限");
            }
            /*登录成功 */
            httpSession.setAttribute(Const.CURRENT_USER,userInfo);
        }
        return serverResponse;
    }


}
