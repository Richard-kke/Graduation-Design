package com.qf;

import com.qf.data.DataPreprocessor;
import com.qf.data.DatabaseInitializer;
import com.qf.data.vo.DataSourceConfig;

public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("电商数据预处理系统 v1.0");
        System.out.println("========================================");

        try {
            // 1. 初始化配置
            DataSourceConfig config = new DataSourceConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/fengmi?characterEncoding=UTF-8");
            config.setUsername("root");
            config.setPassword("root");
            config.setDataWarehouseRoot("D:\\data_warehouse");

            // 2. 初始化数据库表结构
            System.out.println("\n1. 开始初始化数据库表结构...");
            DatabaseInitializer initializer = new DatabaseInitializer(config);
            boolean initResult = initializer.initialize();
            if (!initResult) {
                System.out.println("数据库初始化失败，但继续执行数据预处理...");
            }

            // 3. 数据预处理
            System.out.println("\n2. 开始数据预处理...");
            DataPreprocessor preprocessor = new DataPreprocessor(config);
            boolean preprocessResult = preprocessor.process();
            if (!preprocessResult) {
                System.out.println("数据预处理失败！");
                return;
            }
            System.out.println("数据预处理完成");

            System.out.println("\n========================================");
            System.out.println("数据预处理系统运行完成！");
            System.out.println("========================================");
            System.out.println("\n提示：指标计算和模型训练请使用 EcommerceDataPipeline 类");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("系统运行出错：" + e.getMessage());
        }
    }
}