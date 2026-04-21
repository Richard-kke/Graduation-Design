package com.qf.controller;

import com.qf.common.utils.LogUtils;
import com.qf.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // 提交订单
    @RequestMapping("submitOrder")
    String submitOrder(@RequestParam("goodsId") int goodsId, 
                     @RequestParam("goodsName") String goodsName, 
                     @RequestParam("totalMoney") double totalMoney, 
                     HttpServletRequest request, 
                     HttpSession session) {
        
        // 获取用户ID
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:login.jsp";
        }
        
        // 调用业务层提交订单
        String orderId = orderService.submitOrder(userId, goodsId, totalMoney);
        
        // 记录用户下单日志
        LogUtils.logUserOrder(request, goodsId, goodsName, orderId);
        
        if (orderId != null) {
            return "redirect:confirm.jsp?orderId=" + orderId;
        } else {
            return "redirect:cart.jsp";
        }
    }
    
    // 支付订单
    @RequestMapping("payOrder")
    String payOrder(@RequestParam("orderId") String orderId, 
                  @RequestParam("goodsId") int goodsId, 
                  @RequestParam("goodsName") String goodsName, 
                  @RequestParam("amount") double amount, 
                  HttpServletRequest request) {
        
        // 调用业务层支付订单
        boolean result = orderService.payOrder(orderId, amount);
        
        // 记录用户支付日志
        LogUtils.logUserPayment(request, goodsId, goodsName, orderId, amount);
        
        if (result) {
            return "redirect:orderDetail?id=" + orderId;
        } else {
            return "redirect:pay.jsp?orderId=" + orderId;
        }
    }
}
