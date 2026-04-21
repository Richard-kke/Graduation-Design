package com.qf;

import com.qf.common.utils.DataLayerManager;
import com.qf.common.utils.DataSyncUtils;
import com.qf.common.utils.LogUtils;

import java.util.List;

public class DataSyncMain {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("电商数据采集、同步与分层存储系统");
        System.out.println("========================================");
        
        // 1. 创建数据仓库目录结构
        System.out.println("\n1. 创建数据仓库目录结构...");
        DataLayerManager.createDataWarehouseStructure();
        
        // 2. 显示数据仓库结构
        System.out.println("\n2. 数据仓库目录结构：");
        List<String> structure = DataSyncUtils.getDataWarehouseStructure();
        for (String line : structure) {
            System.out.println(line);
        }
        
        // 3. 执行全量数据同步（从MySQL到ODS层）
        System.out.println("\n3. 执行全量数据同步...");
        DataSyncUtils.executeFullSync();
        
        // 4. 执行完整的数据分层处理
        System.out.println("\n4. 执行数据分层处理...");
        DataLayerManager.executeFullDataProcessing();
        
        // 5. 显示数据分层统计信息
        System.out.println("\n5. 数据分层统计信息：");
        DataLayerManager.getDataLayerStats();
        
        // 6. 验证数据完整性
        System.out.println("\n6. 验证数据完整性...");
        String currentDate = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        DataLayerManager.validateDataIntegrity(currentDate);
        
        System.out.println("\n========================================");
        System.out.println("数据处理流程执行完成！");
        System.out.println("========================================");
    }
}