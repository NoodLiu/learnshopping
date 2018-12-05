package com.neuedu.service;

import com.neuedu.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> selectAll();
}
