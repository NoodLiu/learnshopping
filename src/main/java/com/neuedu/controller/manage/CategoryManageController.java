package com.neuedu.controller.manage;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 商品类别
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/get_category.do")
    public ServerResponse get_category(HttpSession httpSession, Integer categoryId) {
        /* 管理员登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NEDD_LOGIN.getCode(), Const.ReponseCodeEnum.NEDD_LOGIN.getMsg());
        }
        /* 判断权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NOT_POWER.getCode(),Const.ReponseCodeEnum.NOT_POWER.getMsg());
        }

        return categoryService.get_category(categoryId);

    }
}
