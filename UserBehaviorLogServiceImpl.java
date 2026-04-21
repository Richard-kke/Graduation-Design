package com.qf.service.impl;

import com.qf.dao.UserBehaviorLogDao;
import com.qf.domain.UserBehaviorLog;
import com.qf.service.UserBehaviorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class UserBehaviorLogServiceImpl implements UserBehaviorLogService {
    
    @Autowired
    private UserBehaviorLogDao userBehaviorLogDao;
    
    @Override
    public boolean recordUserBehavior(UserBehaviorLog log) {
        int result = userBehaviorLogDao.insert(log);
        return result > 0;
    }
    
    @Override
    public boolean batchRecordUserBehavior(List<UserBehaviorLog> logs) {
        int result = userBehaviorLogDao.batchInsert(logs);
        return result > 0;
    }
    
    @Override
    public List<UserBehaviorLog> getUserBehaviorByUserId(Integer userId) {
        return userBehaviorLogDao.selectByUserId(userId);
    }
    
    @Override
    public List<UserBehaviorLog> getUserBehaviorByCondition(Map<String, Object> condition) {
        return userBehaviorLogDao.selectByCondition(condition);
    }
    
    @Override
    public Map<String, Integer> statisticsBehaviorType() {
        Map<String, Integer> result = new java.util.HashMap<>();
        List<Map<String, Object>> counts = userBehaviorLogDao.countByBehaviorType();
        
        for (Map<String, Object> count : counts) {
            String behaviorType = (String) count.get("behaviorType");
            Integer countValue = ((Number) count.get("count")).intValue();
            result.put(behaviorType, countValue);
        }
        
        return result;
    }
    
    @Override
    public boolean exportUserBehaviorLogs(Map<String, Object> condition, String filePath) {
        List<UserBehaviorLog> logs = userBehaviorLogDao.exportLogs(condition);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID,用户ID,行为类型,商品ID,商品名称,行为详情,行为时间,IP地址,用户代理,会话ID,创建时间\n");
            
            for (UserBehaviorLog log : logs) {
                writer.write(log.getId() + ","
                        + log.getUserId() + ","
                        + log.getBehaviorType() + ","
                        + log.getGoodsId() + ","
                        + log.getGoodsName() + ","
                        + log.getBehaviorDetail() + ","
                        + log.getBehaviorTime() + ","
                        + log.getIpAddress() + ","
                        + log.getUserAgent() + ","
                        + log.getSessionId() + ","
                        + log.getCreateTime() + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean archiveUserBehaviorLogs(String dateStr) {
        Map<String, Object> condition = new java.util.HashMap<>();
        condition.put("behaviorTime", dateStr);
        List<UserBehaviorLog> logs = userBehaviorLogDao.selectByCondition(condition);
        
        if (logs.isEmpty()) {
            return true;
        }
        
        String archivePath = "archive/user_behavior_logs_" + dateStr + ".csv";
        return exportUserBehaviorLogs(condition, archivePath);
    }
    
    @Override
    public Map<String, Object> getUserBehaviorStatistics(Integer userId) {
        Map<String, Object> statistics = new java.util.HashMap<>();
        
        try {
            Map<String, Object> condition = new java.util.HashMap<>();
            condition.put("userId", userId);
            List<UserBehaviorLog> logs = userBehaviorLogDao.selectByCondition(condition);
            
            int totalVisits = logs.size();
            int clickCount = 0;
            int addCartCount = 0;
            int orderCount = 0;
            int payCount = 0;
            
            for (UserBehaviorLog log : logs) {
                String behaviorType = log.getBehaviorType();
                switch (behaviorType) {
                    case "click":
                        clickCount++;
                        break;
                    case "add_cart":
                        addCartCount++;
                        break;
                    case "order":
                        orderCount++;
                        break;
                    case "pay":
                        payCount++;
                        break;
                }
            }
            
            statistics.put("userId", userId);
            statistics.put("totalVisits", totalVisits);
            statistics.put("clickCount", clickCount);
            statistics.put("addCartCount", addCartCount);
            statistics.put("orderCount", orderCount);
            statistics.put("payCount", payCount);
            statistics.put("conversionRate", totalVisits > 0 ? (double) payCount / totalVisits : 0.0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return statistics;
    }
}
