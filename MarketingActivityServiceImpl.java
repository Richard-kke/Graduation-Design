package com.qf.service.impl;

import com.qf.dao.MarketingActivityDao;
import com.qf.domain.MarketingActivity;
import com.qf.service.MarketingActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class MarketingActivityServiceImpl implements MarketingActivityService {
    
    @Autowired
    private MarketingActivityDao marketingActivityDao;
    
    @Override
    public boolean createMarketingActivity(MarketingActivity activity) {
        int result = marketingActivityDao.insert(activity);
        return result > 0;
    }
    
    @Override
    public boolean updateMarketingActivity(MarketingActivity activity) {
        int result = marketingActivityDao.update(activity);
        return result > 0;
    }
    
    @Override
    public MarketingActivity getMarketingActivityById(Long id) {
        return marketingActivityDao.selectById(id);
    }
    
    @Override
    public List<MarketingActivity> getMarketingActivityByCondition(Map<String, Object> condition) {
        return marketingActivityDao.selectByCondition(condition);
    }
    
    @Override
    public List<Map<String, Object>> getActivityEffectivenessStatistics() {
        return marketingActivityDao.statisticsEffectiveness();
    }
    
    @Override
    public boolean exportMarketingActivities(Map<String, Object> condition, String filePath) {
        List<MarketingActivity> activities = marketingActivityDao.selectByCondition(condition);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID,活动名称,活动类型,活动内容,开始时间,结束时间,目标用户,实际用户,展示次数,点击次数,转化次数,收入,状态,创建用户,创建时间,更新时间\n");
            
            for (MarketingActivity activity : activities) {
                writer.write(activity.getId() + ","
                        + activity.getActivityName() + ","
                        + activity.getActivityType() + ","
                        + activity.getActivityContent() + ","
                        + activity.getStartTime() + ","
                        + activity.getEndTime() + ","
                        + activity.getTargetUsers() + ","
                        + activity.getActualUsers() + ","
                        + activity.getImpressions() + ","
                        + activity.getClicks() + ","
                        + activity.getConversions() + ","
                        + activity.getRevenue() + ","
                        + activity.getStatus() + ","
                        + activity.getCreateUser() + ","
                        + activity.getCreateTime() + ","
                        + activity.getUpdateTime() + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean archiveMarketingActivities(String dateStr) {
        Map<String, Object> condition = new java.util.HashMap<>();
        condition.put("endTime", dateStr);
        List<MarketingActivity> activities = marketingActivityDao.selectByCondition(condition);
        
        if (activities.isEmpty()) {
            return true;
        }
        
        String archivePath = "archive/marketing_activities_" + dateStr + ".csv";
        return exportMarketingActivities(condition, archivePath);
    }
}
