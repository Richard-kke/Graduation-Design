package com.qf.service;

import com.qf.domain.UserBehaviorLog;
import java.util.List;
import java.util.Map;

public interface UserBehaviorLogService {
    /**
     * 记录用户行为
     */
    boolean recordUserBehavior(UserBehaviorLog log);
    
    /**
     * 批量记录用户行为
     */
    boolean batchRecordUserBehavior(List<UserBehaviorLog> logs);
    
    /**
     * 根据用户ID查询行为记录
     */
    List<UserBehaviorLog> getUserBehaviorByUserId(Integer userId);
    
    /**
     * 根据条件查询行为记录
     */
    List<UserBehaviorLog> getUserBehaviorByCondition(Map<String, Object> condition);
    
    /**
     * 统计用户行为类型
     */
    Map<String, Integer> statisticsBehaviorType();
    
    /**
     * 导出用户行为记录
     */
    boolean exportUserBehaviorLogs(Map<String, Object> condition, String filePath);
    
    /**
     * 归档用户行为记录
     */
    boolean archiveUserBehaviorLogs(String dateStr);
    
    /**
     * 获取用户行为统计
     */
    Map<String, Object> getUserBehaviorStatistics(Integer userId);
}
