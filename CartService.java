package com.qf.service;

public interface CartService {
    // 添加商品到购物车
    boolean addToCart(Integer userId, int goodsId, int quantity);
    
    // 从购物车移除商品
    boolean removeFromCart(int cartId);
}