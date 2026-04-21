package com.qf.dao;

import com.qf.domain.UserBehaviorLog;
import java.util.List;
import java.util.Map;

public interface UserBehaviorLogDao {
    /**
     * 插入用户行为日志
     */
    int insert(UserBehaviorLog log);
    
    /**
     * 批量插入用户行为日志
     */
    int batchInsert(List<UserBehaviorLog> logs);
    
    /**
     * 根据用户ID查询行为日志
     */
    List<UserBehaviorLog> selectByUserId(Integer userId);
    
    /**
     * 根据条件查询行为日志
     */
    List<UserBehaviorLog> selectByCondition(Map<String, Object> condition);
    
    /**
     * 根据行为类型统计
     */
    List<Map<String, Object>> countByBehaviorType();
    
    /**
     * 导出行为日志
     */
    List<UserBehaviorLog> exportLogs(Map<String, Object> condition);
}
