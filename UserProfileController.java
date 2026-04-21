package com.qf.controller;

import com.qf.common.ApiResponse;
import com.qf.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    @GetMapping("/basic/{userId}")
    public ApiResponse<Map<String, Object>> getUserBasicProfile(@PathVariable Integer userId) {
        Map<String, Object> profile = userProfileService.getUserBasicProfile(userId);
        return ApiResponse.success(profile);
    }
    
    @GetMapping("/rfm/{userId}")
    public ApiResponse<Map<String, Object>> getUserRFMProfile(@PathVariable Integer userId) {
        Map<String, Object> rfmProfile = userProfileService.getUserRFMProfile(userId);
        return ApiResponse.success(rfmProfile);
    }
    
    @GetMapping("/complete/{userId}")
    public ApiResponse<Map<String, Object>> getUserCompleteProfile(@PathVariable Integer userId) {
        Map<String, Object> completeProfile = userProfileService.getUserCompleteProfile(userId);
        return ApiResponse.success(completeProfile);
    }
    
    @PostMapping("/batch")
    public ApiResponse<List<Map<String, Object>>> batchGetUserProfiles(@RequestBody List<Integer> userIds) {
        List<Map<String, Object>> profiles = userProfileService.batchGetUserProfiles(userIds);
        return ApiResponse.success(profiles);
    }
    
    @GetMapping("/cluster/{userId}")
    public ApiResponse<Map<String, Object>> getUserClusterInfo(@PathVariable Integer userId) {
        Map<String, Object> clusterInfo = userProfileService.getUserClusterInfo(userId);
        return ApiResponse.success(clusterInfo);
    }
    
    @GetMapping("/behavior/{userId}")
    public ApiResponse<Map<String, Object>> getUserBehaviorAnalysis(@PathVariable Integer userId) {
        Map<String, Object> behaviorAnalysis = userProfileService.getUserBehaviorAnalysis(userId);
        return ApiResponse.success(behaviorAnalysis);
    }
}
