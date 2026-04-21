package com.qf.controller;

import com.qf.common.ApiResponse;
import com.qf.domain.MarketingActivity;
import com.qf.service.MarketingActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
public class MarketingActivityController {
    
    @Autowired
    private MarketingActivityService marketingActivityService;
    
    @PostMapping("/create")
    public ApiResponse<Void> createMarketingActivity(@RequestBody MarketingActivity activity) {
        boolean result = marketingActivityService.createMarketingActivity(activity);
        return result ? ApiResponse.success("创建成功", null) : ApiResponse.error("创建失败");
    }
    
    @PutMapping("/update")
    public ApiResponse<Void> updateMarketingActivity(@RequestBody MarketingActivity activity) {
        boolean result = marketingActivityService.updateMarketingActivity(activity);
        return result ? ApiResponse.success("更新成功", null) : ApiResponse.error("更新失败");
    }
    
    @GetMapping("/get/{id}")
    public ApiResponse<MarketingActivity> getMarketingActivityById(@PathVariable Long id) {
        MarketingActivity activity = marketingActivityService.getMarketingActivityById(id);
        return ApiResponse.success(activity);
    }
    
    @PostMapping("/query")
    public ApiResponse<List<MarketingActivity>> getMarketingActivityByCondition(@RequestBody Map<String, Object> condition) {
        List<MarketingActivity> activities = marketingActivityService.getMarketingActivityByCondition(condition);
        return ApiResponse.success(activities);
    }
    
    @GetMapping("/effectiveness")
    public ApiResponse<List<Map<String, Object>>> getActivityEffectivenessStatistics() {
        List<Map<String, Object>> statistics = marketingActivityService.getActivityEffectivenessStatistics();
        return ApiResponse.success(statistics);
    }
    
    @PostMapping("/export")
    public ApiResponse<Void> exportMarketingActivities(@RequestBody Map<String, Object> params) {
        String filePath = params.getOrDefault("filePath", "export/marketing_activities.csv").toString();
        Map<String, Object> condition = (Map<String, Object>) params.getOrDefault("condition", new java.util.HashMap<>());
        boolean result = marketingActivityService.exportMarketingActivities(condition, filePath);
        return result ? ApiResponse.success("导出成功", null) : ApiResponse.error("导出失败");
    }
    
    @PostMapping("/archive")
    public ApiResponse<Void> archiveMarketingActivities(@RequestParam String dateStr) {
        boolean result = marketingActivityService.archiveMarketingActivities(dateStr);
        return result ? ApiResponse.success("归档成功", null) : ApiResponse.error("归档失败");
    }
}
