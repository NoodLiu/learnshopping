package com.neuedu.service;

import com.neuedu.common.ServerResponse;

import java.util.Map;

public interface OrderService {
    ServerResponse create(Integer userId,Integer shippingId);

    ServerResponse cancel(Integer userId,Long OrderNo);

    ServerResponse get_order_cart_product(Integer userId);

    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);

    ServerResponse detail(Long orderNo);

    ServerResponse pay(Integer userId,Long orderNo);

    ServerResponse ailpay_callback(Map<String,String> map);

    ServerResponse query_order_pay_stratus(Long orderNo);

    /**
     * 查询订单（定时关闭订单）
     */
    void colseOrder(String time);
}
