package com.neuedu.controller.portal;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/portal/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param httpSession
     * @param shippingId
     * @return
     */
    @RequestMapping("/create.do")
    public ServerResponse create(HttpSession httpSession, Integer shippingId) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return orderService.create(userInfo.getId(), shippingId);
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping("/cancel.do")
    public ServerResponse cancel(HttpSession httpSession, Long OrderNo) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return orderService.cancel(userInfo.getId(), OrderNo);
    }

    /**
     * 获取购物车中订单商品信息
     *
     * @return
     */
    @RequestMapping("/get_order_cart_product.do")
    public ServerResponse get_order_cart_product(HttpSession httpSession) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return orderService.get_order_cart_product(userInfo.getId());
    }

    /**
     * 订单列表
     *
     * @param httpSession
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession httpSession,
                               @RequestParam(defaultValue = "1", required = false) Integer pageNum,
                               @RequestParam(defaultValue = "10", required = false) Integer pagesize) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);

        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return orderService.list(userInfo.getId(), pageNum, pagesize);
    }

    /**
     * 查询订单详情
     *
     * @return
     */
    @RequestMapping("/detail.do")
    public ServerResponse detail(HttpSession httpSession, Long orderNo) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);

        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }

        return orderService.detail(orderNo);
    }

    /**
     * 支付接口
     *
     * @param orderNo
     * @return
     */
    @RequestMapping("/pay.do")
    public ServerResponse pay(HttpSession httpSession, Long orderNo) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        System.out.println("支付接口");
        return orderService.pay(userInfo.getId(), orderNo);
    }

    /**
     * 支付宝服务器回调应用服务器接口
     * @return
     */
    @RequestMapping("/alipay_callback.do")
    public ServerResponse callback(HttpServletRequest request) {
        Map<String,String[]> map = request.getParameterMap();
        Iterator<String> it = map.keySet().iterator();
        Map<String,String> requestparam = Maps.newHashMap();
        while (it.hasNext()){
            String key = it.next();
            String[] strArr = map.get(key);
            String value="";
            for (int i = 0; i < strArr.length; i++) {
                value = (i==strArr.length-1)?value+strArr[i]:value + strArr[i]+",";
            }
            requestparam.put(key,value);
        }
        //step1 支付宝验证签名
        try {
            requestparam.remove("sign_type");
            boolean result = AlipaySignature.rsaCheckV2(requestparam, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!result) {
                return ServerResponse.createServerResponseByError("非法请求，验证不通过");
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        /* 处理业务逻辑 */
        return orderService.ailpay_callback(requestparam);
    }

    /**
     * 查询订单支付状态
     * @param httpSession
     * @param orderNo
     * @return
     */
    @RequestMapping("/query_order_pay_stratus.do")
    public ServerResponse query_order_pay_stratus(HttpSession httpSession,Long orderNo) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("用户未登录");
        }
        return orderService.query_order_pay_stratus(orderNo);
    }
}
