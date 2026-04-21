package com.qf.service.impl;

import com.qf.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    // 添加商品到购物车
    @Override
    public boolean addToCart(Integer userId, int goodsId, int quantity) {
        // 简单实现，返回true表示操作成功
        System.out.println("添加商品到购物车: userId=" + userId + ", goodsId=" + goodsId + ", quantity=" + quantity);
        return true;
    }
    
    // 从购物车移除商品
    @Override
    public boolean removeFromCart(int cartId) {
        // 简单实现，返回true表示操作成功
        System.out.println("从购物车移除商品: cartId=" + cartId);
        return true;
    }
}
