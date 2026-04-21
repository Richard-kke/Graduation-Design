package com.qf.service;

import java.util.Map;

public interface OrderService {
    // 提交订单
    String submitOrder(Integer userId, int goodsId, double totalMoney);
    
    // 支付订单
    boolean payOrder(String orderId, double amount);
    
    // 获取用户订单统计
    Map<String, Object> getUserOrderStatistics(Integer userId);
    
    // 计算用户RFM指标
    Map<String, Object> calculateUserRFM(Integer userId);
}