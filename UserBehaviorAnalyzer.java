package com.qf.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.Serializable;
import java.util.List;

public class UserBehaviorAnalyzer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 计算PV（页面访问量）
    public long calculatePV(JavaSparkContext sc, String logPath) {
        JavaRDD<String> logRDD = sc.textFile(logPath + "\\*.log");
        return logRDD.count();
    }
    
    // 计算UV（独立访客数）
    public long calculateUV(JavaSparkContext sc, String logPath) {
        JavaRDD<String> logRDD = sc.textFile(logPath + "\\*.log");
        return logRDD
                .map(line -> line.split("\\|")[2]) // 提取sessionId
                .distinct()
                .count();
    }
    
    // 计算用户行为转化率
    public double calculateConversionRate(JavaSparkContext sc, String logPath) {
        JavaRDD<String> logRDD = sc.textFile(logPath + "\\*.log");
        
        // 统计总访问量
        long totalVisits = logRDD.count();
        
        // 统计下单量
        long orders = logRDD
                .filter(line -> line.split("\\|")[7].equals("order")) // 提取behaviorType
                .count();
        
        // 计算转化率
        return totalVisits > 0 ? (double) orders / totalVisits : 0;
    }
    
    // 计算热门商品
    public List<String> calculateHotGoods(SQLContext sqlContext, String orderPath, int topN) {
        // 读取订单数据
        Dataset<Row> orderDF = sqlContext.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(orderPath + "\\*.csv");
        
        // 注册临时表
        orderDF.registerTempTable("orders");
        
        // 计算热门商品
        Dataset<Row> hotGoodsDF = sqlContext.sql(
                "SELECT goods_id, goods_name, SUM(goods_num) as total_sales " +
                "FROM orders " +
                "GROUP BY goods_id, goods_name " +
                "ORDER BY total_sales DESC " +
                "LIMIT " + topN
        );
        
        // 转换为List
        return hotGoodsDF.toJavaRDD()
                .map(row -> row.getAs("goods_id") + "," + row.getAs("goods_name") + "," + row.getAs("total_sales"))
                .collect();
    }
    
    // 计算用户活跃度
    public double calculateUserActivity(SQLContext sqlContext, String userPath, String orderPath) {
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
        
        // 计算活跃用户数（有订单的用户）
        Dataset<Row> activeUsersDF = sqlContext.sql(
                "SELECT DISTINCT u.id FROM users u JOIN orders o ON u.id = o.user_id"
        );
        
        // 计算用户活跃度
        long totalUsers = userDF.count();
        long activeUsers = activeUsersDF.count();
        
        return totalUsers > 0 ? (double) activeUsers / totalUsers : 0;
    }
    
    // 计算销售漏斗转化率
    public List<String> calculateFunnelConversion(SQLContext sqlContext, String logPath) {
        // 读取用户行为日志
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
        logDF.registerTempTable("user_behavior");
        
        // 计算各阶段用户数
        Dataset<Row> funnelDF = sqlContext.sql(
                "SELECT behaviorType, COUNT(DISTINCT userId) as userCount " +
                "FROM user_behavior " +
                "WHERE behaviorType IN ('view', 'click', 'add_cart', 'order', 'pay') " +
                "GROUP BY behaviorType " +
                "ORDER BY FIELD(behaviorType, 'view', 'click', 'add_cart', 'order', 'pay')"
        );
        
        // 转换为List
        return funnelDF.toJavaRDD()
                .map(row -> row.getAs("behaviorType") + "," + row.getAs("userCount"))
                .collect();
    }
}
