package com.qf.spark;

import com.qf.config.SparkConfig;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.sql.SQLContext;

import java.util.List;
import java.util.Map;

public class EcommerceAnalyticsMain {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("电商数据分析系统启动");
        System.out.println("========================================");
        
        // 创建Spark上下文
        JavaSparkContext sc = SparkConfig.createJavaSparkContext();
        SQLContext sqlContext = SparkConfig.createSQLContext(sc);
        
        try {
            // 1. 用户行为分析
            System.out.println("\n1. 开始用户行为分析...");
            UserBehaviorAnalyzer behaviorAnalyzer = new UserBehaviorAnalyzer();
            
            // 计算PV
            long pv = behaviorAnalyzer.calculatePV(sc, SparkConfig.ODS_LOG_PATH);
            System.out.println("PV（页面访问量）: " + pv);
            
            // 计算UV
            long uv = behaviorAnalyzer.calculateUV(sc, SparkConfig.ODS_LOG_PATH);
            System.out.println("UV（独立访客数）: " + uv);
            
            // 计算转化率
            double conversionRate = behaviorAnalyzer.calculateConversionRate(sc, SparkConfig.ODS_LOG_PATH);
            System.out.printf("用户行为转化率: %.4f%%\n", conversionRate * 100);
            
            // 计算热门商品
            List<String> hotGoods = behaviorAnalyzer.calculateHotGoods(sqlContext, SparkConfig.ODS_ORDER_PATH, 10);
            System.out.println("热门商品 TOP 10:");
            for (String goods : hotGoods) {
                System.out.println("  " + goods);
            }
            
            // 计算销售漏斗转化率
            List<String> funnelConversion = behaviorAnalyzer.calculateFunnelConversion(sqlContext, SparkConfig.ODS_LOG_PATH);
            System.out.println("销售漏斗转化率:");
            for (String stage : funnelConversion) {
                System.out.println("  " + stage);
            }
            
            // 2. 用户画像构建
            System.out.println("\n2. 开始用户画像构建...");
            UserProfileBuilder profileBuilder = new UserProfileBuilder();
            
            // 构建基本用户画像
            List<String> basicProfiles = profileBuilder.buildUserBasicProfile(sqlContext, SparkConfig.ODS_USER_PATH, SparkConfig.ODS_ORDER_PATH);
            System.out.println("生成用户基本画像: " + basicProfiles.size() + " 条记录");
            
            // 构建RFM画像
            profileBuilder.calculateRFM(sqlContext, SparkConfig.ODS_ORDER_PATH, SparkConfig.ADS_RFM_ANALYSIS_PATH);
            System.out.println("生成用户RFM画像完成");
            
            // 构建完整用户画像
            profileBuilder.buildUserProfile(sqlContext, SparkConfig.ODS_USER_PATH, 
                                           SparkConfig.ODS_ORDER_PATH, 
                                           SparkConfig.ODS_LOG_PATH, 
                                           SparkConfig.ADS_USER_PORTRAIT_PATH);
            System.out.println("生成完整用户画像完成");
            
            // 3. 推荐模型训练
            System.out.println("\n3. 开始推荐模型训练...");
            RecommendationEngine recommendationEngine = new RecommendationEngine();
            
            // 训练协同过滤模型
            MatrixFactorizationModel model = recommendationEngine.trainRecommendationModel(sc, SparkConfig.ODS_ORDER_PATH);
            System.out.println("推荐模型训练完成");
            
            // 为用户推荐商品
            List<String> recommendations = recommendationEngine.recommendProducts(model, 1, 5);
            System.out.println("为用户ID=1推荐商品:");
            for (String rec : recommendations) {
                System.out.println("  " + rec);
            }
            
            // 保存推荐模型
            recommendationEngine.saveModel(model, SparkConfig.RECOMMENDATION_MODEL_PATH);
            
            // 4. 结果输出
            System.out.println("\n4. 数据分析结果输出...");
            
            // 保存用户行为指标
            saveResults("user_behavior_metrics.txt", pv + "," + uv + "," + conversionRate);
            
            // 保存热门商品
            saveResults("hot_goods.txt", hotGoods);
            
            // 保存用户画像
            saveResults("user_profiles.txt", basicProfiles);
            
            // 保存推荐结果
            saveResults("recommendations.txt", recommendations);
            
            System.out.println("\n========================================");
            System.out.println("电商数据分析系统运行完成");
            System.out.println("========================================");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭Spark上下文
            sc.stop();
        }
    }
    
    // 保存结果到文件
    private static void saveResults(String fileName, String content) {
        // 这里可以实现结果保存逻辑
        System.out.println("保存结果到 " + fileName + ": " + content);
    }
    
    // 保存结果列表到文件
    private static void saveResults(String fileName, List<String> results) {
        // 这里可以实现结果列表保存逻辑
        System.out.println("保存结果到 " + fileName + ": " + results.size() + " 条记录");
    }
}
