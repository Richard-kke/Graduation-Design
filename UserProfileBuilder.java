package com.qf.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.Serializable;
import java.util.*;

public class UserProfileBuilder implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 构建用户基本画像
    public List<String> buildUserBasicProfile(SQLContext sqlContext, String userPath, String orderPath) {
        // 读取用户数据
        Dataset<Row> userDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(userPath + "\\*.csv");
        
        // 读取订单数据
        Dataset<Row> orderDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(orderPath + "\\*.csv");
        
        // 注册临时表
        userDF.registerTempTable("users");
        orderDF.registerTempTable("orders");
        
        // 计算用户基本画像
        Dataset<Row> userProfileDF = sqlContext.sql(
                "SELECT u.user_id, u.username, u.gender, u.register_time, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(o.total_money) as total_amount " +
                "FROM users u LEFT JOIN orders o ON u.user_id = o.user_id " +
                "GROUP BY u.user_id, u.username, u.gender, u.register_time"
        );
        
        // 转换为List
        return userProfileDF.toJavaRDD()
                .map(row -> {
                    String user_id = String.valueOf(row.getAs("user_id"));
                    String username = row.getAs("username");
                    String gender = row.getAs("gender");
                    String register_time = row.getAs("register_time");
                    long order_count = 0;
                    try {
                        order_count = row.getAs("order_count") != null ? Long.parseLong(row.getAs("order_count").toString()) : 0;
                    } catch (Exception e) {
                        order_count = 0;
                    }
                    double total_amount = 0.0;
                    try {
                        total_amount = row.getAs("total_amount") != null ? Double.parseDouble(row.getAs("total_amount").toString()) : 0.0;
                    } catch (Exception e) {
                        total_amount = 0.0;
                    }
                    
                    return user_id + "," + username + "," + gender + "," + register_time + "," + order_count + "," + total_amount;
                })
                .collect();
    }
    
    // 计算RFM指标
    public void calculateRFM(SQLContext sqlContext, String orderPath, String outputPath) {
        // 读取订单数据
        Dataset<Row> orderDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(orderPath + "\\*.csv");
        
        // 注册临时表
        orderDF.registerTempTable("orders");
        
        // 计算RFM指标
        Dataset<Row> rfmDF = sqlContext.sql(
                "SELECT user_id, " +
                "MAX(order_time) as last_purchase_time, " +
                "COUNT(DISTINCT order_id) as frequency, " +
                "SUM(total_money) as monetary " +
                "FROM orders " +
                "GROUP BY user_id"
        );
        
        // 注册RFM临时表
        rfmDF.registerTempTable("rfm");
        
        // 计算RFM评分和用户分层
        Dataset<Row> rfmScoredDF = sqlContext.sql(
                "SELECT user_id, last_purchase_time, frequency, monetary, " +
                "CASE " +
                "    WHEN frequency >= 10 THEN 5 " +
                "    WHEN frequency >= 5 THEN 4 " +
                "    WHEN frequency >= 3 THEN 3 " +
                "    WHEN frequency >= 1 THEN 2 " +
                "    ELSE 1 " +
                "END as frequency_score, " +
                "CASE " +
                "    WHEN monetary >= 10000 THEN 5 " +
                "    WHEN monetary >= 5000 THEN 4 " +
                "    WHEN monetary >= 2000 THEN 3 " +
                "    WHEN monetary >= 500 THEN 2 " +
                "    ELSE 1 " +
                "END as monetary_score, " +
                "CASE " +
                "    WHEN DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 7 THEN 5 " +
                "    WHEN DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 30 THEN 4 " +
                "    WHEN DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 90 THEN 3 " +
                "    WHEN DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 180 THEN 2 " +
                "    ELSE 1 " +
                "END as recency_score, " +
                "CASE " +
                "    WHEN frequency >= 10 AND monetary >= 10000 AND DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 7 THEN '高价值用户' " +
                "    WHEN frequency >= 5 AND monetary >= 5000 AND DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 30 THEN '中高价值用户' " +
                "    WHEN frequency >= 3 AND monetary >= 2000 AND DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 90 THEN '中等价值用户' " +
                "    WHEN frequency >= 1 AND monetary >= 500 AND DATEDIFF(CURRENT_DATE(), last_purchase_time) <= 180 THEN '低价值用户' " +
                "    ELSE '流失用户' " +
                "END as user_segment " +
                "FROM rfm"
        );
        
        // 保存RFM结果到文件
        rfmScoredDF.write()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .save(outputPath);
    }
    
    // 构建完整用户画像
    public void buildUserProfile(SQLContext sqlContext, String userPath, String orderPath, String logPath, String outputPath) {
        // 读取用户数据
        Dataset<Row> userDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(userPath + "\\*.csv");
        
        // 读取订单数据
        Dataset<Row> orderDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(orderPath + "\\*.csv");
        
        // 读取日志数据
        Dataset<Row> logDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "false")
                .option("delimiter", "|")
                .option("inferSchema", "true")
                .load(logPath + "\\*.log");
        
        // 添加列名
        String[] columnNames = {"logId", "userId", "sessionId", "timestamp", "ipAddress", "userAgent", "behaviorType", "goodsId", "pageUrl", "referrerUrl", "deviceType", "osType", "browserType", "actionContent", "extraInfo"};
        for (int i = 0; i < columnNames.length; i++) {
            logDF = logDF.withColumnRenamed("_c" + i, columnNames[i]);
        }
        
        // 注册临时表
        userDF.registerTempTable("users");
        orderDF.registerTempTable("orders");
        logDF.registerTempTable("user_log");
        
        // 构建用户画像
        Dataset<Row> userProfileDF = sqlContext.sql(
                "SELECT u.user_id, u.username, u.gender, u.register_time, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(o.total_money) as total_amount, " +
                "COUNT(l.logId) as visit_count, " +
                "COUNT(DISTINCT l.sessionId) as session_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'click' THEN 1 END) as click_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'add_cart' THEN 1 END) as add_cart_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'order' THEN 1 END) as order_behavior_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'pay' THEN 1 END) as pay_behavior_count " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.user_id = o.user_id " +
                "LEFT JOIN user_log l ON u.user_id = l.userId " +
                "GROUP BY u.user_id, u.username, u.gender, u.register_time"
        );
        
        // 保存用户画像到文件
        userProfileDF.write()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .save(outputPath);
    }
    
    // 用户聚类分析
    public KMeansModel clusterUsers(JavaSparkContext sc, SQLContext sqlContext, String userPath, String orderPath, String logPath) {
        // 读取用户数据
        Dataset<Row> userDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(userPath + "\\*.csv");
        
        // 读取订单数据
        Dataset<Row> orderDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(orderPath + "\\*.csv");
        
        // 读取日志数据
        Dataset<Row> logDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "false")
                .option("delimiter", "|")
                .option("inferSchema", "true")
                .load(logPath + "\\*.log");
        
        // 添加列名
        String[] columnNames = {"logId", "userId", "sessionId", "timestamp", "ipAddress", "userAgent", "behaviorType", "goodsId", "pageUrl", "referrerUrl", "deviceType", "osType", "browserType", "actionContent", "extraInfo"};
        for (int i = 0; i < columnNames.length; i++) {
            logDF = logDF.withColumnRenamed("_c" + i, columnNames[i]);
        }
        
        // 注册临时表
        userDF.registerTempTable("users");
        orderDF.registerTempTable("orders");
        logDF.registerTempTable("user_log");
        
        // 计算用户特征
        Dataset<Row> userFeaturesDF = sqlContext.sql(
                "SELECT u.user_id, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(o.total_money) as total_amount, " +
                "COUNT(l.logId) as visit_count, " +
                "COUNT(DISTINCT l.sessionId) as session_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'click' THEN 1 END) as click_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'add_cart' THEN 1 END) as add_cart_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'order' THEN 1 END) as order_behavior_count, " +
                "COUNT(CASE WHEN l.behaviorType = 'pay' THEN 1 END) as pay_behavior_count " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.user_id = o.user_id " +
                "LEFT JOIN user_log l ON u.user_id = l.userId " +
                "GROUP BY u.user_id"
        );
        
        // 转换为Vector
        JavaRDD<Vector> featuresRDD = userFeaturesDF.toJavaRDD()
                .map(row -> {
                    double order_count = 0.0;
                    double total_amount = 0.0;
                    double visit_count = 0.0;
                    double session_count = 0.0;
                    double click_count = 0.0;
                    double add_cart_count = 0.0;
                    double order_behavior_count = 0.0;
                    double pay_behavior_count = 0.0;
                    
                    try {
                        order_count = row.getAs("order_count") != null ? Double.parseDouble(row.getAs("order_count").toString()) : 0.0;
                        total_amount = row.getAs("total_amount") != null ? Double.parseDouble(row.getAs("total_amount").toString()) : 0.0;
                        visit_count = row.getAs("visit_count") != null ? Double.parseDouble(row.getAs("visit_count").toString()) : 0.0;
                        session_count = row.getAs("session_count") != null ? Double.parseDouble(row.getAs("session_count").toString()) : 0.0;
                        click_count = row.getAs("click_count") != null ? Double.parseDouble(row.getAs("click_count").toString()) : 0.0;
                        add_cart_count = row.getAs("add_cart_count") != null ? Double.parseDouble(row.getAs("add_cart_count").toString()) : 0.0;
                        order_behavior_count = row.getAs("order_behavior_count") != null ? Double.parseDouble(row.getAs("order_behavior_count").toString()) : 0.0;
                        pay_behavior_count = row.getAs("pay_behavior_count") != null ? Double.parseDouble(row.getAs("pay_behavior_count").toString()) : 0.0;
                    } catch (Exception e) {
                        // 保持默认值为0
                    }
                    
                    return Vectors.dense(order_count, total_amount, visit_count, session_count, click_count, add_cart_count, order_behavior_count, pay_behavior_count);
                });
        
        // 训练KMeans模型
        int numClusters = 5;
        int numIterations = 20;
        KMeansModel model = KMeans.train(featuresRDD.rdd(), numClusters, numIterations);
        
        // 评估模型
        double cost = KMeans.computeCost(featuresRDD.rdd(), model.clusterCenters());
        System.out.println("K-Means聚类成本: " + cost);
        
        // 打印聚类中心
        System.out.println("聚类中心:");
        for (Vector center : model.clusterCenters()) {
            System.out.println(center);
        }
        
        return model;
    }
}