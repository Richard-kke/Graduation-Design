package com.qf.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 训练推荐模型
    public MatrixFactorizationModel trainRecommendationModel(JavaSparkContext sc, String orderPath) {
        // 读取订单数据
        JavaRDD<String> orderRDD = sc.textFile(orderPath + "\\*.csv");
        
        // 转换为Rating格式
        JavaRDD<Rating> ratingsRDD = orderRDD
                .filter(line -> !line.startsWith("order_id")) // 跳过表头
                .map(line -> {
                    String[] parts = line.split(",");
                    if (parts.length < 12) {
                        return null;
                    }
                    
                    try {
                        int userId = Integer.parseInt(parts[1]);
                        int goodsId = Integer.parseInt(parts[8]);
                        double rating = 5.0; // 默认评分，可以根据实际情况调整
                        
                        return new Rating(userId, goodsId, rating);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(rating -> rating != null);
        
        // 训练ALS模型
        int rank = 10;
        int numIterations = 10;
        double lambda = 0.01;
        MatrixFactorizationModel model = ALS.train(ratingsRDD.rdd(), rank, numIterations, lambda);
        
        return model;
    }
    
    // 为用户推荐商品
    public List<String> recommendProducts(MatrixFactorizationModel model, int userId, int numRecommendations) {
        // 为用户推荐商品
        Rating[] recommendations = model.recommendProducts(userId, numRecommendations);
        
        // 转换为List
        List<String> results = new ArrayList<>();
        for (Rating recommendation : recommendations) {
            results.add(recommendation.user() + "," + recommendation.product() + "," + recommendation.rating());
        }
        
        return results;
    }
    
    // 为商品推荐用户
    public List<String> recommendUsers(MatrixFactorizationModel model, int productId, int numRecommendations) {
        // 为商品推荐用户
        Rating[] recommendations = model.recommendUsers(productId, numRecommendations);
        
        // 转换为List
        List<String> results = new ArrayList<>();
        for (Rating recommendation : recommendations) {
            results.add(recommendation.user() + "," + recommendation.product() + "," + recommendation.rating());
        }
        
        return results;
    }
    
    // 保存推荐模型
    public void saveModel(MatrixFactorizationModel model, String modelPath) {
        // 保存模型
        File dir = new File(modelPath);
        if (dir.exists()) {
            // 删除已存在的模型文件
            deleteDir(dir);
        }
        
        // 使用模型自带的SparkContext
        model.save(model.productFeatures().context(), modelPath);
        System.out.println("推荐模型已保存到：" + modelPath);
    }
    
    // 加载推荐模型
    public MatrixFactorizationModel loadModel(JavaSparkContext sc, String modelPath) {
        // 加载模型
        return MatrixFactorizationModel.load(sc.sc(), modelPath);
    }
    
    // 删除目录
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDir(file);
                }
            }
        }
        dir.delete();
    }
    
    // 基于内容的推荐
    public List<String> contentBasedRecommendation(JavaSparkContext sc, String goodsPath, int goodsId, int numRecommendations) {
        // 这里可以实现基于内容的推荐
        // 例如：根据商品的分类、标签等相似性推荐
        
        // 简化实现，返回空列表
        return new ArrayList<>();
    }
    
    // 基于协同过滤的推荐
    public List<String> collaborativeFilteringRecommendation(JavaSparkContext sc, String orderPath, int userId, int numRecommendations) {
        // 训练模型
        MatrixFactorizationModel model = trainRecommendationModel(sc, orderPath);
        
        // 为用户推荐商品
        return recommendProducts(model, userId, numRecommendations);
    }
    
    // 混合推荐（协同过滤 + 基于内容）
    public List<String> hybridRecommendation(JavaSparkContext sc, String orderPath, String goodsPath, int userId, int numRecommendations) {
        // 这里可以实现混合推荐
        // 例如：结合协同过滤和基于内容的推荐结果
        
        // 简化实现，返回协同过滤推荐结果
        return collaborativeFilteringRecommendation(sc, orderPath, userId, numRecommendations);
    }
    
    // 评估推荐模型
    public double evaluateModel(MatrixFactorizationModel model, JavaRDD<Rating> testRatings) {
        // 评估模型
        // 计算预测评分与实际评分的均方误差
        
        // 简化实现，返回0.0
        return 0.0;
    }
}
