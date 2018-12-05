package com.neuedu.controller;

import com.neuedu.entity.Cart;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/list")
    public List<Cart> cartlist() {
        List<Cart> carts = cartService.selectAll();
        return carts;
    }
}
