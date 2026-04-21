package com.qf.common.utils;

import com.qf.common.vo.UserBehaviorLog;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class LogUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Random RANDOM = new Random();
    
    public static UserBehaviorLog createUserBehaviorLog(HttpServletRequest request, String behaviorType, int goodsId, String actionContent) {
        UserBehaviorLog log = new UserBehaviorLog();
        
        // 生成唯一日志ID
        log.setLogId(generateLogId());
        
        // 获取用户ID（从Session中获取，未登录则为0）
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        log.setUserId(userId != null ? userId : 0);
        
        // 获取或生成Session ID
        String sessionId = request.getSession().getId();
        log.setSessionId(sessionId);
        
        // 设置时间戳
        log.setTimestamp(new Date());
        
        // 获取IP地址
        String ip = getClientIp(request);
        log.setIpAddress(ip);
        
        // 获取User-Agent
        String userAgent = request.getHeader("User-Agent");
        log.setUserAgent(userAgent);
        
        // 设置行为类型
        log.setBehaviorType(behaviorType);
        
        // 设置商品ID
        log.setGoodsId(goodsId);
        
        // 设置页面URL和来源URL
        log.setPageUrl(request.getRequestURL().toString());
        log.setReferrerUrl(request.getHeader("Referer"));
        
        // 解析设备类型、操作系统和浏览器
        parseUserAgent(log, userAgent);
        
        // 设置行为内容
        log.setActionContent(actionContent);
        
        // 额外信息
        log.setExtraInfo("platform=web");
        
        return log;
    }
    
    private static String generateLogId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    private static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    private static void parseUserAgent(UserBehaviorLog log, String userAgent) {
        if (userAgent == null) {
            return;
        }
        
        // 简单解析设备类型
        if (userAgent.contains("Mobile")) {
            log.setDeviceType("mobile");
        } else if (userAgent.contains("Tablet")) {
            log.setDeviceType("tablet");
        } else {
            log.setDeviceType("desktop");
        }
        
        // 简单解析操作系统
        if (userAgent.contains("Windows")) {
            log.setOsType("Windows");
        } else if (userAgent.contains("Mac OS")) {
            log.setOsType("Mac OS");
        } else if (userAgent.contains("Android")) {
            log.setOsType("Android");
        } else if (userAgent.contains("iOS")) {
            log.setOsType("iOS");
        } else {
            log.setOsType("Other");
        }
        
        // 简单解析浏览器
        if (userAgent.contains("Chrome")) {
            log.setBrowserType("Chrome");
        } else if (userAgent.contains("Firefox")) {
            log.setBrowserType("Firefox");
        } else if (userAgent.contains("Safari")) {
            log.setBrowserType("Safari");
        } else if (userAgent.contains("Edge")) {
            log.setBrowserType("Edge");
        } else if (userAgent.contains("IE")) {
            log.setBrowserType("IE");
        } else {
            log.setBrowserType("Other");
        }
    }
    
    public static void writeLogToFile(UserBehaviorLog log, String logDirectory) {
        try {
            // 创建日志文件名（按日期）
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(log.getTimestamp());
            String fileName = logDirectory + "/user_behavior_" + dateStr + ".log";
            
            // 格式化日志内容
            StringBuilder sb = new StringBuilder();
            sb.append(log.getLogId()).append("|")
              .append(log.getUserId()).append("|")
              .append(log.getSessionId()).append("|")
              .append(DATE_FORMAT.format(log.getTimestamp())).append("|")
              .append(log.getIpAddress()).append("|")
              .append(log.getUserAgent()).append("|")
              .append(log.getBehaviorType()).append("|")
              .append(log.getGoodsId()).append("|")
              .append(log.getPageUrl()).append("|")
              .append(log.getReferrerUrl() != null ? log.getReferrerUrl() : "-").append("|")
              .append(log.getDeviceType()).append("|")
              .append(log.getOsType()).append("|")
              .append(log.getBrowserType()).append("|")
              .append(log.getActionContent()).append("|")
              .append(log.getExtraInfo()).append("\n");
            
            // 写入文件
            FileUtils.writeFile(fileName, sb.toString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void logUserView(HttpServletRequest request, int goodsId, String goodsName) {
        UserBehaviorLog log = createUserBehaviorLog(request, "view", goodsId, "View goods: " + goodsName);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
    
    public static void logUserClick(HttpServletRequest request, int goodsId, String goodsName) {
        UserBehaviorLog log = createUserBehaviorLog(request, "click", goodsId, "Click goods: " + goodsName);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
    
    public static void logAddToCart(HttpServletRequest request, int goodsId, String goodsName, int quantity) {
        UserBehaviorLog log = createUserBehaviorLog(request, "add_cart", goodsId, "Add to cart: " + goodsName + ", quantity: " + quantity);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
    
    public static void logRemoveFromCart(HttpServletRequest request, int goodsId, String goodsName) {
        UserBehaviorLog log = createUserBehaviorLog(request, "remove_cart", goodsId, "Remove from cart: " + goodsName);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
    
    public static void logUserOrder(HttpServletRequest request, int goodsId, String goodsName, String orderId) {
        UserBehaviorLog log = createUserBehaviorLog(request, "order", goodsId, "Create order: " + orderId + ", goods: " + goodsName);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
    
    public static void logUserPayment(HttpServletRequest request, int goodsId, String goodsName, String orderId, double amount) {
        UserBehaviorLog log = createUserBehaviorLog(request, "pay", goodsId, "Pay order: " + orderId + ", amount: " + amount + ", goods: " + goodsName);
        writeLogToFile(log, "D:\\data_warehouse\\ods\\log");
    }
}