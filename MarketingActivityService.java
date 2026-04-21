package com.qf.service;

import com.qf.domain.MarketingActivity;
import java.util.List;
import java.util.Map;

public interface MarketingActivityService {
    /**
     * 创建营销活动
     */
    boolean createMarketingActivity(MarketingActivity activity);
    
    /**
     * 更新营销活动
     */
    boolean updateMarketingActivity(MarketingActivity activity);
    
    /**
     * 根据ID获取营销活动
     */
    MarketingActivity getMarketingActivityById(Long id);
    
    /**
     * 根据条件查询营销活动
     */
    List<MarketingActivity> getMarketingActivityByCondition(Map<String, Object> condition);
    
    /**
     * 获取活动效果统计
     */
    List<Map<String, Object>> getActivityEffectivenessStatistics();
    
    /**
     * 导出营销活动记录
     */
    boolean exportMarketingActivities(Map<String, Object> condition, String filePath);
    
    /**
     * 归档营销活动记录
     */
    boolean archiveMarketingActivities(String dateStr);
}
