package com.qf.controller;

import com.qf.common.ApiResponse;
import com.qf.domain.UserBehaviorLog;
import com.qf.service.UserBehaviorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-behavior")
public class UserBehaviorLogController {
    
    @Autowired
    private UserBehaviorLogService userBehaviorLogService;
    
    @PostMapping("/record")
    public ApiResponse<Void> recordUserBehavior(@RequestBody UserBehaviorLog log) {
        boolean result = userBehaviorLogService.recordUserBehavior(log);
        return result ? ApiResponse.success("记录成功", null) : ApiResponse.error("记录失败");
    }
    
    @PostMapping("/batch-record")
    public ApiResponse<Void> batchRecordUserBehavior(@RequestBody List<UserBehaviorLog> logs) {
        boolean result = userBehaviorLogService.batchRecordUserBehavior(logs);
        return result ? ApiResponse.success("批量记录成功", null) : ApiResponse.error("批量记录失败");
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserBehaviorLog>> getUserBehaviorByUserId(@PathVariable Integer userId) {
        List<UserBehaviorLog> logs = userBehaviorLogService.getUserBehaviorByUserId(userId);
        return ApiResponse.success(logs);
    }
    
    @PostMapping("/query")
    public ApiResponse<List<UserBehaviorLog>> getUserBehaviorByCondition(@RequestBody Map<String, Object> condition) {
        List<UserBehaviorLog> logs = userBehaviorLogService.getUserBehaviorByCondition(condition);
        return ApiResponse.success(logs);
    }
    
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Integer>> statisticsBehaviorType() {
        Map<String, Integer> statistics = userBehaviorLogService.statisticsBehaviorType();
        return ApiResponse.success(statistics);
    }
    
    @PostMapping("/export")
    public ApiResponse<Void> exportUserBehaviorLogs(@RequestBody Map<String, Object> params) {
        String filePath = params.getOrDefault("filePath", "export/user_behavior_logs.csv").toString();
        Map<String, Object> condition = (Map<String, Object>) params.getOrDefault("condition", new java.util.HashMap<>());
        boolean result = userBehaviorLogService.exportUserBehaviorLogs(condition, filePath);
        return result ? ApiResponse.success("导出成功", null) : ApiResponse.error("导出失败");
    }
    
    @PostMapping("/archive")
    public ApiResponse<Void> archiveUserBehaviorLogs(@RequestParam String dateStr) {
        boolean result = userBehaviorLogService.archiveUserBehaviorLogs(dateStr);
        return result ? ApiResponse.success("归档成功", null) : ApiResponse.error("归档失败");
    }
}
