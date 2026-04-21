package com.qf.controller;

import com.qf.common.utils.LogUtils;
import com.qf.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    // 添加到购物车
    @RequestMapping("addToCart")
    String addToCart(@RequestParam("goodsId") int goodsId, 
                   @RequestParam("quantity") int quantity, 
                   @RequestParam("goodsName") String goodsName, 
                   HttpServletRequest request, 
                   HttpSession session) {
        
        // 获取用户ID
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            // 未登录用户，重定向到登录页
            return "redirect:login.jsp";
        }
        
        // 调用业务层添加到购物车
        boolean result = cartService.addToCart(userId, goodsId, quantity);
        
        // 记录用户添加购物车日志
        LogUtils.logAddToCart(request, goodsId, goodsName, quantity);
        
        if (result) {
            return "redirect:cartSuccess.jsp";
        } else {
            return "redirect:goodsDetail?id=" + goodsId;
        }
    }
    
    // 从购物车移除
    @RequestMapping("removeFromCart")
    String removeFromCart(@RequestParam("cartId") int cartId, 
                        @RequestParam("goodsId") int goodsId, 
                        @RequestParam("goodsName") String goodsName, 
                        HttpServletRequest request) {
        
        // 调用业务层从购物车移除
        boolean result = cartService.removeFromCart(cartId);
        
        // 记录用户移除购物车日志
        LogUtils.logRemoveFromCart(request, goodsId, goodsName);
        
        return "redirect:cart.jsp";
    }
}
