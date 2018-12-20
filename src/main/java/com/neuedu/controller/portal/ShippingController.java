package com.neuedu.controller.portal;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.Shipping;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/portal/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    /**
     * 添加地址
     * @param httpSession
     * @param shipping
     * @return
     */
    @RequestMapping("/add.do")
    public ServerResponse add(HttpSession httpSession, Shipping shipping){
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return shippingService.add(userInfo.getId(),shipping);
    }

    /**
     * 删除地址
     * @param httpSession
     * @param shippingId
     * @return
     */
    @RequestMapping("/delete.do")
    public ServerResponse delete(HttpSession httpSession, Integer shippingId){
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return shippingService.delete(userInfo.getId(),shippingId);
    }

    /**
     * 登录状态下修改地址
     * @return
     */
    @RequestMapping("/update.do")
    public ServerResponse update(HttpSession httpSession, Shipping shipping){
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        shipping.setUserId(userInfo.getId());
        return shippingService.update(shipping);
    }

    /**
     * 查看地址详情
     * @param httpSession
     * @param shippingId
     * @return
     */
    @RequestMapping("/select.do")
    public ServerResponse select(HttpSession httpSession, Integer shippingId){
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return shippingService.select(userInfo.getId(),shippingId);
    }

    /**
     * 查看地址列表
     * @param httpSession
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession httpSession,
                               @RequestParam(defaultValue = "1",required = false)Integer pageNum,
                               @RequestParam(defaultValue = "10",required = false)Integer pageSize) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return shippingService.list(pageNum,pageSize);
    }

}
