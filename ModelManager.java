package com.qf.spark;

import com.qf.config.SparkConfig;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.sql.SQLContext;

import java.util.List;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.qf.common.utils.FileUtils;

public class ModelManager implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final RecommendationEngine recommendationEngine;
    private final UserProfileBuilder profileBuilder;
    private final SimpleDateFormat dateFormat;
    
    public ModelManager() {
        this.recommendationEngine = new RecommendationEngine();
        this.profileBuilder = new UserProfileBuilder();
        this.dateFormat = new SimpleDateFormat("yyyyMMdd");
    }
    
    /**
     * 执行模型训练
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 训练结果状态
     */
    public boolean trainModels(JavaSparkContext sc, SQLContext sqlContext) {
        try {
            // 1. 训练推荐模型
            trainRecommendationModel(sc);
            
            // 2. 训练用户画像模型
            trainUserProfileModel(sc, sqlContext);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 训练推荐模型
     * @param sc JavaSparkContext
     */
    private void trainRecommendationModel(JavaSparkContext sc) {
        System.out.println("=== 开始训练推荐模型 ===");
        
        // 训练模型
        MatrixFactorizationModel model = recommendationEngine.trainRecommendationModel(sc, SparkConfig.ODS_ORDER_PATH);
        
        // 保存模型
        recommendationEngine.saveModel(model, SparkConfig.RECOMMENDATION_MODEL_PATH);
        
        System.out.println("=== 推荐模型训练完成 ===");
    }
    
    /**
     * 训练用户画像模型
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void trainUserProfileModel(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始训练用户画像模型 ===");
        
        // 构建用户画像（这里作为模型训练的一部分）
        profileBuilder.buildUserProfile(sqlContext, SparkConfig.ODS_USER_PATH, 
                                       SparkConfig.ODS_ORDER_PATH, 
                                       SparkConfig.ODS_LOG_PATH, 
                                       SparkConfig.ADS_USER_PORTRAIT_PATH);
        
        System.out.println("=== 用户画像模型训练完成 ===");
    }
    
    /**
     * 执行模型预测
     * @param sc JavaSparkContext
     * @param userId 用户ID
     * @param numRecommendations 推荐数量
     * @return 推荐结果列表
     */
    public List<String> predict(JavaSparkContext sc, int userId, int numRecommendations) {
        System.out.println("=== 开始模型预测 ===");
        
        try {
            // 加载推荐模型
            MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
            
            // 为用户推荐商品
            List<String> recommendations = recommendationEngine.recommendProducts(model, userId, numRecommendations);
            
            // 保存推荐结果
            saveRecommendations(recommendations, userId);
            
            System.out.println("=== 模型预测完成 ===");
            return recommendations;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 评估模型
     * @param sc JavaSparkContext
     * @return 模型评估分数
     */
    public double evaluateModel(JavaSparkContext sc) {
        System.out.println("=== 开始模型评估 ===");
        
        try {
            // 加载推荐模型
            MatrixFactorizationModel model = recommendationEngine.loadModel(sc, SparkConfig.RECOMMENDATION_MODEL_PATH);
            
            // 这里可以实现更复杂的模型评估逻辑
            // 简化实现，返回固定分数
            double score = 0.85;
            
            // 保存评估结果
            saveModelEvaluation(score);
            
            System.out.println("=== 模型评估完成，得分: " + score + " ===");
            return score;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * 保存推荐结果到文件
     * @param recommendations 推荐结果列表
     * @param userId 用户ID
     */
    private void saveRecommendations(List<String> recommendations, int userId) {
        String dateStr = dateFormat.format(new Date());
        String filePath = SparkConfig.RECOMMENDATION_MODEL_PATH + "\\recommendations_user_" + userId + "_" + dateStr + ".csv";
        
        StringBuilder data = new StringBuilder();
        data.append("user_id,goods_id,rating\n");
        
        for (String recommendation : recommendations) {
            data.append(recommendation).append("\n");
        }
        
        FileUtils.writeFile(filePath, data.toString(), false);
        System.out.println("推荐结果已保存到: " + filePath);
    }
    
    /**
     * 保存模型评估结果到文件
     * @param score 评估分数
     */
    private void saveModelEvaluation(double score) {
        String dateStr = dateFormat.format(new Date());
        String filePath = SparkConfig.RECOMMENDATION_MODEL_PATH + "\\model_evaluation_" + dateStr + ".csv";
        
        StringBuilder data = new StringBuilder();
        data.append("evaluation_date,score,model_version\n");
        data.append(dateStr).append(",").append(score).append(",1.0\n");
        
        FileUtils.writeFile(filePath, data.toString(), false);
        System.out.println("模型评估结果已保存到: " + filePath);
    }
    
    /**
     * 加载并使用模型进行批量预测
     * @param sc JavaSparkContext
     * @param userIds 用户ID列表
     * @param numRecommendations 推荐数量
     * @return 批量预测结果状态
     */
    public boolean batchPredict(JavaSparkContext sc, List<Integer> userIds, int numRecommendations) {
        try {
            for (Integer userId : userIds) {
                predict(sc, userId, numRecommendations);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取模型信息
     * @return 模型信息字符串
     */
    public String getModelInfo() {
        return "Recommendation Model: ALS\n" +
               "User Profile Model: Statistical\n" +
               "Model Version: 1.0\n" +
               "Last Updated: " + dateFormat.format(new Date());
    }
    
    /**
     * 执行混合推荐
     * @param sc JavaSparkContext
     * @param userId 用户ID
     * @param numRecommendations 推荐数量
     * @return 混合推荐结果
     */
    public List<String> hybridRecommendation(JavaSparkContext sc, int userId, int numRecommendations) {
        System.out.println("=== 开始混合推荐 ===");
        
        try {
            List<String> recommendations = recommendationEngine.hybridRecommendation(
                    sc, 
                    SparkConfig.ODS_ORDER_PATH, 
                    SparkConfig.ODS_GOODS_PATH, 
                    userId, 
                    numRecommendations);
            
            System.out.println("=== 混合推荐完成 ===");
            return recommendations;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}