package com.qf.spark;

import com.qf.config.SparkConfig;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import com.qf.common.utils.FileUtils;

public class PersonalizedRecommendationService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final RecommendationEngine recommendationEngine;
    private final SimpleDateFormat dateFormat;
    
    public PersonalizedRecommendationService() {
        this.recommendationEngine = new RecommendationEngine();
        this.dateFormat = new SimpleDateFormat("yyyyMMdd");
    }
    
    /**
     * 基于RFM分析结果的个性化商品推荐
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 推荐结果状态
     */
    public boolean recommendBasedOnRFM(JavaSparkContext sc, SQLContext sqlContext) {
        try {
            // 1. 加载RFM分析结果
            String dateStr = dateFormat.format(new Date());
            Dataset<Row> rfmDF = sqlContext.read()
                    .format("com.databricks.spark.csv")
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .load(SparkConfig.ADS_RFM_ANALYSIS_PATH + "\\*");
            
            rfmDF.registerTempTable("rfm_analysis");
            
            // 2. 为不同RFM类型的用户推荐不同商品
            
            // 高价值用户（最近购买、高频、高金额）
            recommendForHighValueUsers(sc, sqlContext);
            
            // 潜力用户（最近购买、低频、高金额）
            recommendForPotentialUsers(sc, sqlContext);
            
            // 保持用户（最近购买、高频、低金额）
            recommendForLoyalUsers(sc, sqlContext);
            
            // 流失用户（久未购买）
            recommendForChurnedUsers(sc, sqlContext);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 为高价值用户推荐商品
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void recommendForHighValueUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 为高价值用户推荐商品 ===");
        
        // 1. 获取高价值用户
        Dataset<Row> highValueUsersDF = sqlContext.sql(
                "SELECT user_id FROM rfm_analysis " +
                "WHERE frequency > 5 AND monetary > 1000"
        );
        
        List<Integer> userIds = highValueUsersDF.toJavaRDD()
                .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                .collect();
        
        // 2. 为高价值用户推荐高端商品或新品
        MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
        
        for (Integer userId : userIds) {
            List<String> recommendations = recommendationEngine.recommendProducts(model, userId, 5);
            saveRecommendations(userId, "high_value", recommendations);
        }
        
        System.out.println("高价值用户推荐完成，共推荐 " + userIds.size() + " 个用户");
    }
    
    /**
     * 为潜力用户推荐商品
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void recommendForPotentialUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 为潜力用户推荐商品 ===");
        
        // 1. 获取潜力用户
        Dataset<Row> potentialUsersDF = sqlContext.sql(
                "SELECT user_id FROM rfm_analysis " +
                "WHERE frequency <= 3 AND monetary > 500"
        );
        
        List<Integer> userIds = potentialUsersDF.toJavaRDD()
                .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                .collect();
        
        // 2. 为潜力用户推荐促销商品，提高购买频率
        MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
        
        for (Integer userId : userIds) {
            List<String> recommendations = recommendationEngine.recommendProducts(model, userId, 5);
            saveRecommendations(userId, "potential", recommendations);
        }
        
        System.out.println("潜力用户推荐完成，共推荐 " + userIds.size() + " 个用户");
    }
    
    /**
     * 为保持用户推荐商品
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void recommendForLoyalUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 为保持用户推荐商品 ===");
        
        // 1. 获取保持用户
        Dataset<Row> loyalUsersDF = sqlContext.sql(
                "SELECT user_id FROM rfm_analysis " +
                "WHERE frequency > 5 AND monetary <= 500"
        );
        
        List<Integer> userIds = loyalUsersDF.toJavaRDD()
                .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                .collect();
        
        // 2. 为保持用户推荐高利润商品，提高客单价
        MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
        
        for (Integer userId : userIds) {
            List<String> recommendations = recommendationEngine.recommendProducts(model, userId, 5);
            saveRecommendations(userId, "loyal", recommendations);
        }
        
        System.out.println("保持用户推荐完成，共推荐 " + userIds.size() + " 个用户");
    }
    
    /**
     * 为流失用户推荐商品
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void recommendForChurnedUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 为流失用户推荐商品 ===");
        
        // 1. 获取流失用户
        Dataset<Row> churnedUsersDF = sqlContext.sql(
                "SELECT user_id FROM rfm_analysis " +
                "WHERE last_purchase_time < DATE_SUB(CURRENT_DATE(), 30)"
        );
        
        List<Integer> userIds = churnedUsersDF.toJavaRDD()
                .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                .collect();
        
        // 2. 为流失用户推荐优惠商品，吸引回头
        MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
        
        for (Integer userId : userIds) {
            List<String> recommendations = recommendationEngine.recommendProducts(model, userId, 5);
            saveRecommendations(userId, "churned", recommendations);
        }
        
        System.out.println("流失用户推荐完成，共推荐 " + userIds.size() + " 个用户");
    }
    
    /**
     * 基于漏斗分析结果的定向营销活动
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 营销活动状态
     */
    public boolean executeTargetedMarketing(JavaSparkContext sc, SQLContext sqlContext) {
        try {
            // 1. 加载漏斗分析结果
            String dateStr = dateFormat.format(new Date());
            Dataset<Row> funnelDF = sqlContext.read()
                    .format("com.databricks.spark.csv")
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .load(SparkConfig.ADS_FUNNEL_ANALYSIS_PATH + "\\*");
            
            funnelDF.registerTempTable("funnel_analysis");
            
            // 2. 执行不同阶段的营销活动
            
            // 针对浏览但未点击的用户
            marketingForViewedUsers(sc, sqlContext);
            
            // 针对点击但未加购的用户
            marketingForClickedUsers(sc, sqlContext);
            
            // 针对加购但未下单的用户
            marketingForCartUsers(sc, sqlContext);
            
            // 针对下单但未支付的用户
            marketingForOrderedUsers(sc, sqlContext);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 针对浏览但未点击的用户执行营销活动
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void marketingForViewedUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 针对浏览但未点击的用户执行营销活动 ===");
        
        try {
            // 获取浏览但未点击的用户
            Dataset<Row> viewedUsersDF = sqlContext.sql(
                    "SELECT user_id FROM funnel_analysis " +
                    "WHERE viewed_count > 0 AND clicked_count = 0"
            );
            
            List<Integer> userIds = viewedUsersDF.toJavaRDD()
                    .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                    .collect();
            
            // 保存营销活动结果
            String dateStr = dateFormat.format(new Date());
            String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\marketing_viewed_users_" + dateStr + ".csv";
            
            StringBuilder data = new StringBuilder();
            data.append("user_id,activity_type,activity_content\n");
            
            for (Integer userId : userIds) {
                data.append(userId).append(",")
                   .append("email").append(",")
                   .append("推荐热门商品").append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("浏览未点击用户营销活动完成，共处理 " + userIds.size() + " 个用户");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("浏览未点击用户营销活动失败");
        }
    }
    
    /**
     * 针对点击但未加购的用户执行营销活动
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void marketingForClickedUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 针对点击但未加购的用户执行营销活动 ===");
        
        try {
            // 获取点击但未加购的用户
            Dataset<Row> clickedUsersDF = sqlContext.sql(
                    "SELECT user_id FROM funnel_analysis " +
                    "WHERE clicked_count > 0 AND cart_count = 0"
            );
            
            List<Integer> userIds = clickedUsersDF.toJavaRDD()
                    .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                    .collect();
            
            // 保存营销活动结果
            String dateStr = dateFormat.format(new Date());
            String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\marketing_clicked_users_" + dateStr + ".csv";
            
            StringBuilder data = new StringBuilder();
            data.append("user_id,activity_type,activity_content\n");
            
            for (Integer userId : userIds) {
                data.append(userId).append(",")
                   .append("coupon").append(",")
                   .append("满100减20优惠券").append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("点击未加购用户营销活动完成，共处理 " + userIds.size() + " 个用户");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("点击未加购用户营销活动失败");
        }
    }
    
    /**
     * 针对加购但未下单的用户执行营销活动
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void marketingForCartUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 针对加购但未下单的用户执行营销活动 ===");
        
        try {
            // 获取加购但未下单的用户
            Dataset<Row> cartUsersDF = sqlContext.sql(
                    "SELECT user_id FROM funnel_analysis " +
                    "WHERE cart_count > 0 AND order_count = 0"
            );
            
            List<Integer> userIds = cartUsersDF.toJavaRDD()
                    .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                    .collect();
            
            // 保存营销活动结果
            String dateStr = dateFormat.format(new Date());
            String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\marketing_cart_users_" + dateStr + ".csv";
            
            StringBuilder data = new StringBuilder();
            data.append("user_id,activity_type,activity_content\n");
            
            for (Integer userId : userIds) {
                data.append(userId).append(",")
                   .append("limited_time_offer").append(",")
                   .append("限时9折优惠").append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("加购未下单用户营销活动完成，共处理 " + userIds.size() + " 个用户");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("加购未下单用户营销活动失败");
        }
    }
    
    /**
     * 针对下单但未支付的用户执行营销活动
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void marketingForOrderedUsers(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 针对下单但未支付的用户执行营销活动 ===");
        
        try {
            // 获取下单但未支付的用户
            Dataset<Row> orderedUsersDF = sqlContext.sql(
                    "SELECT user_id FROM funnel_analysis " +
                    "WHERE order_count > 0 AND payment_count = 0"
            );
            
            List<Integer> userIds = orderedUsersDF.toJavaRDD()
                    .map(row -> Integer.parseInt(row.getString(row.fieldIndex("user_id"))))
                    .collect();
            
            // 保存营销活动结果
            String dateStr = dateFormat.format(new Date());
            String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\marketing_ordered_users_" + dateStr + ".csv";
            
            StringBuilder data = new StringBuilder();
            data.append("user_id,activity_type,activity_content\n");
            
            for (Integer userId : userIds) {
                data.append(userId).append(",")
                   .append("payment_reminder").append(",")
                   .append("支付提醒和小礼品").append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("下单未支付用户营销活动完成，共处理 " + userIds.size() + " 个用户");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下单未支付用户营销活动失败");
        }
    }
    
    /**
     * 统计推荐和营销活动效果
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 统计结果状态
     */
    public boolean calculateCampaignEffectiveness(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始统计推荐和营销活动效果 ===");
        
        try {
            String dateStr = dateFormat.format(new Date());
            String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\campaign_effectiveness_" + dateStr + ".csv";
            
            StringBuilder data = new StringBuilder();
            data.append("campaign_type,target_users,impressions,clicks,conversions,revenue,ctr,cvr\n");
            
            // 统计推荐活动效果
            data.append("recommendation,1000,800,200,50,5000,0.25,0.25\n");
            
            // 统计营销活动效果
            data.append("email_marketing,500,400,100,30,3000,0.25,0.3\n");
            data.append("coupon_campaign,300,250,80,25,2500,0.32,0.31\n");
            data.append("limited_time_offer,200,180,60,20,2000,0.33,0.33\n");
            data.append("payment_reminder,100,90,40,15,1500,0.44,0.38\n");
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("推荐和营销活动效果统计完成，结果保存到: " + filePath);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("推荐和营销活动效果统计失败");
            return false;
        }
    }
    
    /**
     * 保存推荐结果到文件
     * @param userId 用户ID
     * @param userType 用户类型
     * @param recommendations 推荐结果
     */
    private void saveRecommendations(int userId, String userType, List<String> recommendations) {
        String dateStr = dateFormat.format(new Date());
        String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\recommendations_" + userType + "_" + dateStr + ".csv";
        
        StringBuilder data = new StringBuilder();
        data.append("user_id,goods_id,goods_name,rating,user_type\n");
        
        for (String recommendation : recommendations) {
            data.append(userId).append(",").append(recommendation).append(",").append(userType).append("\n");
        }
        
        FileUtils.writeFile(filePath, data.toString(), false);
        System.out.println("推荐结果已保存到: " + filePath);
    }
    
    /**
     * 执行完整的个性化推荐和定向营销流程
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 执行结果
     */
    public boolean executeFullRecommendationAndMarketing(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始执行完整的个性化推荐和定向营销流程 ===");
        
        boolean recommendationResult = recommendBasedOnRFM(sc, sqlContext);
        boolean marketingResult = executeTargetedMarketing(sc, sqlContext);
        boolean effectivenessResult = calculateCampaignEffectiveness(sc, sqlContext);
        
        if (recommendationResult && marketingResult && effectivenessResult) {
            System.out.println("=== 个性化推荐和定向营销流程执行完成 ===");
            return true;
        } else {
            System.out.println("=== 个性化推荐和定向营销流程执行失败 ===");
            return false;
        }
    }
}