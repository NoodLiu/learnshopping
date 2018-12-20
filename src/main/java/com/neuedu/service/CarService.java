package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface CarService {

    ServerResponse add(Integer userId,Integer productId,Integer count);
    ServerResponse list(Integer userId);

    ServerResponse update(Integer userId,Integer productId,Integer count);

    ServerResponse delete_product(Integer userId,String productId);

    ServerResponse select(Integer userId,Integer productId,Integer check);
    ServerResponse select_All(Integer userId,Integer check);

    ServerResponse get_cart_product_count(Integer userId);
}
