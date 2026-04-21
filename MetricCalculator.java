package com.qf.spark;

import com.qf.config.SparkConfig;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.util.List;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.qf.common.utils.FileUtils;

public class MetricCalculator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final UserBehaviorAnalyzer behaviorAnalyzer;
    private final UserProfileBuilder profileBuilder;
    private final SimpleDateFormat dateFormat;
    
    public MetricCalculator() {
        this.behaviorAnalyzer = new UserBehaviorAnalyzer();
        this.profileBuilder = new UserProfileBuilder();
        this.dateFormat = new SimpleDateFormat("yyyyMMdd");
    }
    
    /**
     * 执行所有指标计算
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     * @return 计算结果状态
     */
    public boolean calculateMetrics(JavaSparkContext sc, SQLContext sqlContext) {
        try {
            // 1. 计算基础指标
            calculateBasicMetrics(sc, sqlContext);
            
            // 2. 计算RFM指标
            calculateRFM(sc, sqlContext);
            
            // 3. 计算漏斗分析指标
            calculateFunnelMetrics(sc, sqlContext);
            
            // 4. 计算用户画像
            buildUserProfiles(sc, sqlContext);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 计算基础指标
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void calculateBasicMetrics(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始计算基础指标 ===");
        
        // 计算PV
        long pv = behaviorAnalyzer.calculatePV(sc, SparkConfig.ODS_LOG_PATH);
        System.out.println("PV: " + pv);
        
        // 计算UV
        long uv = behaviorAnalyzer.calculateUV(sc, SparkConfig.ODS_LOG_PATH);
        System.out.println("UV: " + uv);
        
        // 计算转化率
        double conversionRate = behaviorAnalyzer.calculateConversionRate(sc, SparkConfig.ODS_LOG_PATH);
        System.out.println("转化率: " + String.format("%.4f", conversionRate));
        
        // 计算用户活跃度
        double activeRate = behaviorAnalyzer.calculateUserActivity(sqlContext, SparkConfig.ODS_USER_PATH, SparkConfig.ODS_ORDER_PATH);
        System.out.println("用户活跃度: " + String.format("%.4f", activeRate));
        
        // 计算热门商品
        List<String> hotGoods = behaviorAnalyzer.calculateHotGoods(sqlContext, SparkConfig.ODS_ORDER_PATH, 10);
        System.out.println("热门商品TOP10:");
        for (String goods : hotGoods) {
            System.out.println(goods);
        }
        
        // 保存基础指标到文件
        saveBasicMetricsToFile(pv, uv, conversionRate, activeRate, hotGoods);
        System.out.println("=== 基础指标计算完成 ===");
    }
    
    /**
     * 计算RFM指标
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void calculateRFM(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始计算RFM指标 ===");
        
        // 这里调用UserProfileBuilder的RFM计算方法
        profileBuilder.calculateRFM(sqlContext, SparkConfig.ODS_ORDER_PATH, SparkConfig.ADS_RFM_ANALYSIS_PATH);
        
        System.out.println("=== RFM指标计算完成 ===");
    }
    
    /**
     * 计算漏斗分析指标
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void calculateFunnelMetrics(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始计算漏斗分析指标 ===");
        
        // 计算漏斗转化率
        List<String> funnelData = behaviorAnalyzer.calculateFunnelConversion(sqlContext, SparkConfig.ODS_LOG_PATH);
        
        System.out.println("漏斗分析数据:");
        for (String data : funnelData) {
            System.out.println(data);
        }
        
        // 保存漏斗数据到文件
        saveFunnelDataToFile(funnelData);
        System.out.println("=== 漏斗分析指标计算完成 ===");
    }
    
    /**
     * 构建用户画像
     * @param sc JavaSparkContext
     * @param sqlContext SQLContext
     */
    private void buildUserProfiles(JavaSparkContext sc, SQLContext sqlContext) {
        System.out.println("=== 开始构建用户画像 ===");
        
        // 调用UserProfileBuilder构建用户画像
        profileBuilder.buildUserProfile(sqlContext, SparkConfig.ODS_USER_PATH, 
                                       SparkConfig.ODS_ORDER_PATH, 
                                       SparkConfig.ODS_LOG_PATH, 
                                       SparkConfig.ADS_USER_PORTRAIT_PATH);
        
        System.out.println("=== 用户画像构建完成 ===");
    }
    
    /**
     * 保存基础指标到文件
     * @param pv 页面访问量
     * @param uv 独立访客数
     * @param conversionRate 转化率
     * @param activeRate 用户活跃度
     * @param hotGoods 热门商品列表
     */
    private void saveBasicMetricsToFile(long pv, long uv, double conversionRate, double activeRate, List<String> hotGoods) {
        String dateStr = dateFormat.format(new Date());
        String filePath = SparkConfig.ADS_SALES_REPORT_PATH + "\\basic_metrics_" + dateStr + ".csv";
        
        StringBuilder data = new StringBuilder();
        data.append("metric_name,metric_value\n");
        data.append("pv,").append(pv).append("\n");
        data.append("uv,").append(uv).append("\n");
        data.append("conversion_rate,").append(String.format("%.4f", conversionRate)).append("\n");
        data.append("active_rate,").append(String.format("%.4f", activeRate)).append("\n");
        data.append("\n");
        data.append("hot_goods_rank,goods_id,goods_name,total_sales\n");
        
        for (int i = 0; i < hotGoods.size(); i++) {
            data.append((i + 1)).append(",").append(hotGoods.get(i)).append("\n");
        }
        
        FileUtils.writeFile(filePath, data.toString(), false);
        System.out.println("基础指标已保存到: " + filePath);
    }
    
    /**
     * 保存漏斗数据到文件
     * @param funnelData 漏斗数据列表
     */
    private void saveFunnelDataToFile(List<String> funnelData) {
        String dateStr = dateFormat.format(new Date());
        String filePath = SparkConfig.ADS_FUNNEL_ANALYSIS_PATH + "\\funnel_metrics_" + dateStr + ".csv";
        
        StringBuilder data = new StringBuilder();
        data.append("behavior_type,user_count,conversion_rate\n");
        
        // 保存各阶段用户数
        for (String line : funnelData) {
            data.append(line).append(",").append("0.0000").append("\n");
        }
        
        FileUtils.writeFile(filePath, data.toString(), false);
        System.out.println("漏斗数据已保存到: " + filePath);
    }
}