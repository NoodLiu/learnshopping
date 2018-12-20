package com.neuedu.controller.manage;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 获取商品类别子节点（平级）
     * @param httpSession
     * @param categoryId
     * @return
     */
    @GetMapping("/get_category.do")
    public ServerResponse get_category(HttpSession httpSession, Integer categoryId) {
        /* 判断管理员登录 */
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

    /**
     * 增加商品节点
     * @param httpSession
     * @param parentId
     * @param categoryName
     * @return
     */
    @GetMapping("/add_category")
    public ServerResponse add_category(HttpSession httpSession,
                                       @RequestParam(defaultValue = "0",required = false) Integer parentId,
                                       String categoryName){
        /* 判断管理员登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NEDD_LOGIN.getCode(), Const.ReponseCodeEnum.NEDD_LOGIN.getMsg());
        }
        /* 判断权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NOT_POWER.getCode(),Const.ReponseCodeEnum.NOT_POWER.getMsg());
        }

        return categoryService.add_category(parentId,categoryName);
    }

    /**
     * 修改节点
     * @param httpSession
     * @param categoryId
     * @param categoryName
     * @return
     */
    @GetMapping("/set_category_name.do")
    public ServerResponse set_category_name(HttpSession httpSession,
                                            Integer categoryId,
                                            String categoryName){
        /* 判断管理员登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NEDD_LOGIN.getCode(), Const.ReponseCodeEnum.NEDD_LOGIN.getMsg());
        }
        /* 判断权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NOT_POWER.getCode(),Const.ReponseCodeEnum.NOT_POWER.getMsg());
        }

        return categoryService.set_category_name(categoryId,categoryName);
    }

    /**
     * 获取当前分类节点及其子节点
     * @param httpSession
     * @param categoryId
     * @return
     */
    @GetMapping("/get_deep_name.do")
    public ServerResponse get_deep_name(HttpSession httpSession,
                                            Integer categoryId){
        /* 判断管理员登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NEDD_LOGIN.getCode(), Const.ReponseCodeEnum.NEDD_LOGIN.getMsg());
        }
        /* 判断权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NOT_POWER.getCode(),Const.ReponseCodeEnum.NOT_POWER.getMsg());
        }

        return categoryService.get_deep_name(categoryId);
    }


}
