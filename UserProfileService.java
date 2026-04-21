package com.qf.service;

import java.util.List;
import java.util.Map;

public interface UserProfileService {
    /**
     * 获取用户基本画像
     */
    Map<String, Object> getUserBasicProfile(Integer userId);
    
    /**
     * 获取用户RFM指标
     */
    Map<String, Object> getUserRFMProfile(Integer userId);
    
    /**
     * 获取用户完整画像
     */
    Map<String, Object> getUserCompleteProfile(Integer userId);
    
    /**
     * 批量获取用户画像
     */
    List<Map<String, Object>> batchGetUserProfiles(List<Integer> userIds);
    
    /**
     * 获取用户聚类信息
     */
    Map<String, Object> getUserClusterInfo(Integer userId);
    
    /**
     * 获取用户行为分析
     */
    Map<String, Object> getUserBehaviorAnalysis(Integer userId);
}
