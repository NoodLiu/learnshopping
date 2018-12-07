package com.neuedu.controller.manage;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserMangerService;
import com.neuedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private UserMangerService userMangerService;

    /**
     * 登录
     *
     * @return
     */
    @GetMapping("/login")
    public ServerResponse login(HttpSession httpSession,String username, String password) {
        ServerResponse serverResponse = userMangerService.login(username, password);
        if (serverResponse.isSuccess()){
             /*登录成功 */
            UserInfo userInfo = (UserInfo) serverResponse.getData();
            httpSession.setAttribute(Const.CURRENT_USER,userInfo);
        }
        return serverResponse;
    }


}
