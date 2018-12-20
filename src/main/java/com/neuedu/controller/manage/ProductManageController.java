package com.neuedu.controller.manage;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.Product;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.neuedu.common.Const.CURRENT_USER;

/**
 * 后台
 */
@RequestMapping("manage/product")
@RestController
public class ProductManageController {

    @Autowired
    private ProductService productService;

    /**
     * 商品的增加和修改
     *
     * @param httpSession
     * @param product
     * @return
     */
    @RequestMapping("/save.do")
    public ServerResponse saveOrUpdate(HttpSession httpSession, Product product) {
        /* step1 判断是否登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        /* step2 判断用户权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError("用户权限不足");
        }
        return productService.saveOrUpdate(product);
    }

    /**
     * 商品上下架
     *
     * @param httpSession
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("/set_sale_status.do")
    public ServerResponse set_sale_status(HttpSession httpSession, Integer productId, Integer status) {
        /* step1 判断是否登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        /* step2 判断用户权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError("用户权限不足");
        }
        return productService.set_sale_status(productId,status);
    }

    /**
     * 商品详情
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("/detail.do")
    public ServerResponse detail(HttpSession httpSession,Integer productId){
        /* step1 判断是否登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        /* step2 判断用户权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError("用户权限不足");
        }
        /* step3  */
        return productService.detail(productId);
    }

    /**
     * 商品列表
     * @param httpSession
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession httpSession, @RequestParam(defaultValue = "1",required = false) Integer pageNum,
                               @RequestParam(defaultValue = "10",required = false) Integer pageSize){
        /* step1 判断是否登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        /* step2 判断用户权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError("用户权限不足");
        }
        /* step3  */
        return productService.list(pageNum,pageSize);
    }

    /**
     * 模糊查询
     * @return
     */
    @RequestMapping("/search.do")
    public ServerResponse search(HttpSession httpSession,@RequestParam(defaultValue = "1",required = false) Integer pageNum,
                                    @RequestParam(defaultValue = "10",required = false) Integer pageSize,
                                 @RequestParam(required = false) String productName,
                                 @RequestParam(required = false) Integer productId){
        /* step1 判断是否登录 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
         /*step2 判断用户权限 */
        if (userInfo.getRole() != Const.RoleEnumn.ROLE_ROOT.getCode()) {
            return ServerResponse.createServerResponseByError("用户权限不足");
        }
        /* step3  */
        return productService.search(productId,productName,pageNum,pageSize);
    }

}
