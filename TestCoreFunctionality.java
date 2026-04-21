package com.qf;

import com.qf.service.UserProfileService;
import com.qf.service.MarketingActivityService;
import com.qf.service.UserBehaviorLogService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class TestCoreFunctionality {
    
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("测试核心功能是否正常工作");
        System.out.println("=====================================");
        
        try {
            // 使用Spring Boot方式加载应用上下文
            System.out.println("\n正在初始化Spring Boot应用上下文...");
            ApplicationContext context = SpringApplication.run(Application.class, args);
            
            // 测试用户画像服务
            System.out.println("\n1. 测试用户画像服务");
            UserProfileService userProfileService = context.getBean(UserProfileService.class);
            
            if (userProfileService != null) {
                System.out.println("✓ 用户画像服务初始化成功");
            } else {
                System.out.println("✗ 用户画像服务初始化失败");
            }
            
            // 测试营销活动服务
            System.out.println("\n2. 测试营销活动服务");
            MarketingActivityService marketingActivityService = context.getBean(MarketingActivityService.class);
            
            if (marketingActivityService != null) {
                System.out.println("✓ 营销活动服务初始化成功");
            } else {
                System.out.println("✗ 营销活动服务初始化失败");
            }
            
            // 测试用户行为日志服务
            System.out.println("\n3. 测试用户行为日志服务");
            UserBehaviorLogService userBehaviorLogService = context.getBean(UserBehaviorLogService.class);
            
            if (userBehaviorLogService != null) {
                System.out.println("✓ 用户行为日志服务初始化成功");
            } else {
                System.out.println("✗ 用户行为日志服务初始化失败");
            }
            
            System.out.println("\n=====================================");
            System.out.println("核心功能测试完成！");
            System.out.println("=====================================");
            
        } catch (Exception e) {
            System.out.println("测试过程中出现错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
