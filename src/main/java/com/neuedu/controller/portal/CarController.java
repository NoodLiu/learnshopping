package com.neuedu.controller.portal;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/portal/car")
public class CarController {


    @Autowired
    private CarService carService;

    /**
     * 购物车中添加/修改商品
     *
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("/add.do")
    public ServerResponse add(HttpSession httpSession, Integer productId, Integer count) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return carService.add(userInfo.getId(), productId, count);

    }

    /**
     * 购物车列表
     *
     * @param httpSession
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession httpSession) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return carService.list(userInfo.getId());
    }

    /**
     * 更新购车中商品的数量
     *
     * @param httpSession
     * @return
     */
    @RequestMapping("/update.do")
    public ServerResponse update(HttpSession httpSession, Integer productId, Integer count) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return carService.update(userInfo.getId(), productId, count);
    }

    /**
     * 购物车移除商品
     *
     * @param httpSession
     * @param productIds
     * @return
     */
    @RequestMapping("/delete_product.do")
    public ServerResponse delete_product(HttpSession httpSession, String productIds) {
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.delete_product(userInfo.getId(), productIds);
    }

    /**
     * 选中 商品
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("/select.do")
    public ServerResponse select(HttpSession httpSession,Integer productId){
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.select(userInfo.getId(), productId,Const.Checked.ISCHECK.getCode());
    }

    /**
     * 取消选中商品
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("/un_select.do")
    public ServerResponse un_select(HttpSession httpSession,Integer productId){
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.select(userInfo.getId(), productId,Const.Checked.NOCHECK.getCode());
    }

    /**
     * 全选
     * @param httpSession
     * @param
     * @return
     */
    @RequestMapping("/select_all.do")
    public ServerResponse select_all(HttpSession httpSession){
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.select_All(userInfo.getId(),Const.Checked.ISCHECK.getCode());
    }

    /**
     * 取消全选
     * @param httpSession
     * @return
     */
    @RequestMapping("/un_select_all.do")
    public ServerResponse un_select_all(HttpSession httpSession){
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.select_All(userInfo.getId(),Const.Checked.NOCHECK.getCode());
    }

    /**
     * 获取商品数量
     * @param httpSession
     * @return
     */
    @RequestMapping("/get_cart_product_count.do")
    public ServerResponse get_cart_product_count(HttpSession httpSession) {
        /* 登录校验 */
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return carService.get_cart_product_count(userInfo.getId());
    }

}
