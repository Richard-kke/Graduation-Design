package com.qf;

import com.qf.data.DataPreprocessor;
import com.qf.data.vo.DataSourceConfig;
import com.qf.config.SparkConfig;
import com.qf.spark.MetricCalculator;
import com.qf.spark.ModelManager;
import com.qf.spark.PersonalizedRecommendationService;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.util.Arrays;
import java.util.List;

public class EcommerceDataPipeline {
    
    public static void main(String[] args) {
        System.out.println("=== 电商数据处理管道启动 ===");
        
        try {
            // 1. 初始化配置
            DataSourceConfig dataSourceConfig = initDataSourceConfig();
            
            // 2. 执行数据预处理
            System.out.println("\n--- 1. 数据预处理阶段 ---");
            DataPreprocessor preprocessor = new DataPreprocessor(dataSourceConfig);
            boolean preprocessResult = preprocessor.process();
            
            if (!preprocessResult) {
                System.out.println("数据预处理失败，程序终止");
                return;
            }
            System.out.println("数据预处理完成");
            
            // 3. 初始化Spark环境
            System.out.println("\n--- 2. 初始化Spark环境 ---");
            JavaSparkContext sc = SparkConfig.createJavaSparkContext();
            SQLContext sqlContext = SparkConfig.createSQLContext(sc);
            
            // 4. 执行指标计算
            System.out.println("\n--- 3. 指标计算阶段 ---");
            MetricCalculator metricCalculator = new MetricCalculator();
            boolean metricsResult = metricCalculator.calculateMetrics(sc, sqlContext);
            
            if (!metricsResult) {
                System.out.println("指标计算失败");
            } else {
                System.out.println("指标计算完成");
            }
            
            // 5. 执行模型训练
            System.out.println("\n--- 4. 模型训练阶段 ---");
            ModelManager modelManager = new ModelManager();
            boolean modelResult = modelManager.trainModels(sc, sqlContext);
            
            if (!modelResult) {
                System.out.println("模型训练失败");
            } else {
                System.out.println("模型训练完成");
            }
            
            // 6. 执行模型预测示例
            System.out.println("\n--- 5. 模型预测示例 ---");
            List<Integer> testUserIds = Arrays.asList(1, 2, 3, 4, 5);
            boolean predictResult = modelManager.batchPredict(sc, testUserIds, 5);
            
            if (!predictResult) {
                System.out.println("模型预测失败");
            } else {
                System.out.println("模型预测完成");
            }
            
            // 7. 执行个性化推荐和定向营销
            System.out.println("\n--- 6. 个性化推荐和定向营销 ---");
            PersonalizedRecommendationService recommendationService = new PersonalizedRecommendationService();
            boolean marketingResult = recommendationService.executeFullRecommendationAndMarketing(sc, sqlContext);
            
            if (!marketingResult) {
                System.out.println("个性化推荐和定向营销失败");
            } else {
                System.out.println("个性化推荐和定向营销完成");
            }
            
            // 8. 输出模型信息
            System.out.println("\n--- 7. 模型信息 ---");
            System.out.println(modelManager.getModelInfo());
            
            // 9. 关闭Spark资源
            System.out.println("\n--- 8. 关闭资源 ---");
            sc.stop();
            
            System.out.println("\n=== 电商数据处理管道执行完成 ===");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("程序执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化数据源配置
     * @return 数据源配置对象
     */
    private static DataSourceConfig initDataSourceConfig() {
        DataSourceConfig config = new DataSourceConfig();
        
        // 设置数据库连接信息（从配置文件读取更优，这里简化处理）
        config.setJdbcUrl("jdbc:mysql://localhost:3306/fengmi?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        config.setUsername("root");
        config.setPassword("root");
        
        // 设置数据仓库根目录
        config.setDataWarehouseRoot("D:\\data_warehouse");
        
        // 设置线程池大小
        config.setThreadPoolSize(4);
        
        return config;
    }
}