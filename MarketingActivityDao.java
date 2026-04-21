package com.qf.dao;

import com.qf.domain.MarketingActivity;
import java.util.List;
import java.util.Map;

public interface MarketingActivityDao {
    /**
     * 插入营销活动
     */
    int insert(MarketingActivity activity);
    
    /**
     * 更新营销活动
     */
    int update(MarketingActivity activity);
    
    /**
     * 根据ID查询营销活动
     */
    MarketingActivity selectById(Long id);
    
    /**
     * 根据条件查询营销活动
     */
    List<MarketingActivity> selectByCondition(Map<String, Object> condition);
    
    /**
     * 根据状态查询营销活动
     */
    List<MarketingActivity> selectByStatus(String status);
    
    /**
     * 统计营销活动效果
     */
    List<Map<String, Object>> statisticsEffectiveness();
    
    /**
     * 导出营销活动记录
     */
    List<MarketingActivity> exportActivities(Map<String, Object> condition);
}
