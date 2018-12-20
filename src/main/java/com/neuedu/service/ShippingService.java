package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.entity.Shipping;

public interface ShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId,Integer shippingId);

    ServerResponse update(Shipping shipping);

    ServerResponse select(Integer userId,Integer shippingId);

    ServerResponse list(Integer pageNum,Integer pageSize);
}
