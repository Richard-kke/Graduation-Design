package com.qf.common.utils;

import com.qf.config.DBConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataMappingUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    // 数据映射配置（业务字段到数据仓库字段的映射）
    
    // 用户数据映射
    public static final String[][] USER_FIELD_MAPPING = {
        {"id", "user_id"},           // 业务字段 -> 数据仓库字段
        {"username", "username"},     // 用户名
        {"email", "email"},         // 邮箱
        {"gender", "gender"},       // 性别
        {"create_time", "createtime"}, // 创建时间
        {"status", "flag"}          // 状态
    };
    
    // 商品数据映射
    public static final String[][] GOODS_FIELD_MAPPING = {
        {"id", "goods_id"},          // 商品ID
        {"name", "goods_name"},      // 商品名称
        {"price", "price"},          // 价格
        {"create_time", "pubdate"},   // 发布时间
        {"category_id", "typeName"},  // 分类ID
        {"description", "intro"},     // 商品描述
        {"image_url", "picture"},     // 图片URL
        {"status", "flag"},           // 状态
        {"rating", "star"}           // 评分
    };
    
    // 订单数据映射
    public static final String[][] ORDER_FIELD_MAPPING = {
        {"id", "order_id"},           // 订单ID
        {"user_id", "user_id"},       // 用户ID
        {"create_time", "order_time"}, // 订单时间
        {"total_amount", "total_money"}, // 总金额
        {"status", "order_status"},   // 订单状态
        {"receiver_name", "recipient_name"}, // 收件人姓名
        {"receiver_phone", "recipient_phone"}, // 收件人电话
        {"receiver_address", "recipient_detail"} // 收件人地址
    };
    
    // 订单明细数据映射
    public static final String[][] ORDER_DETAIL_FIELD_MAPPING = {
        {"order_id", "order_id"},     // 订单ID
        {"product_id", "goods_id"},   // 商品ID
        {"product_name", "goods_name"}, // 商品名称
        {"product_price", "goods_price"}, // 商品价格
        {"quantity", "goods_num"}     // 商品数量
    };
    
    // 购物车数据映射
    public static final String[][] CART_FIELD_MAPPING = {
        {"id", "cart_id"},           // 购物车ID
        {"user_id", "user_id"},       // 用户ID
        {"product_id", "goods_id"},   // 商品ID
        {"quantity", "goods_num"},    // 商品数量
        {"status", "cart_status"}     // 购物车状态
    };
    
    // 获取映射后的字段名
    public static String getMappedFieldName(String[][] mapping, String businessField) {
        for (String[] map : mapping) {
            if (map[0].equals(businessField)) {
                return map[1];
            }
        }
        return businessField; // 默认返回原字段名
    }
    
    // 生成数据仓库表名（带日期分区）
    public static String generateWarehouseTableName(String baseTableName) {
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        return baseTableName + "_" + dateStr;
    }
    
    // 获取ODS层数据文件路径
    public static String getOdsFilePath(String dataType) {
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        String basePath = "D:\\data_warehouse\\ods";
        
        switch (dataType.toLowerCase()) {
            case "user":
                return basePath + "\\user\\user_data_" + dateStr + ".csv";
            case "goods":
                return basePath + "\\goods\\goods_data_" + dateStr + ".csv";
            case "order":
                return basePath + "\\order\\order_data_" + dateStr + ".csv";
            case "cart":
                return basePath + "\\cart\\cart_data_" + dateStr + ".csv";
            case "log":
                return basePath + "\\log\\user_behavior_" + dateStr + ".log";
            default:
                return basePath + "\\other\\other_data_" + dateStr + ".csv";
        }
    }
    
    // 获取DWD层数据文件路径
    public static String getDwdFilePath(String dataType) {
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        String basePath = "D:\\data_warehouse\\dwd";
        
        switch (dataType.toLowerCase()) {
            case "user":
                return basePath + "\\user_detail\\user_detail_" + dateStr + ".csv";
            case "goods":
                return basePath + "\\goods_detail\\goods_detail_" + dateStr + ".csv";
            case "order":
                return basePath + "\\order_detail\\order_detail_" + dateStr + ".csv";
            case "cart":
                return basePath + "\\cart_detail\\cart_detail_" + dateStr + ".csv";
            case "behavior":
                return basePath + "\\user_behavior_detail\\user_behavior_detail_" + dateStr + ".csv";
            default:
                return basePath + "\\other_detail\\other_detail_" + dateStr + ".csv";
        }
    }
    
    // 获取DWM层数据文件路径
    public static String getDwmFilePath(String dataType) {
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        String basePath = "D:\\data_warehouse\\dwm";
        
        switch (dataType.toLowerCase()) {
            case "user":
                return basePath + "\\user_summary\\user_summary_" + dateStr + ".csv";
            case "goods":
                return basePath + "\\goods_summary\\goods_summary_" + dateStr + ".csv";
            case "order":
                return basePath + "\\order_summary\\order_summary_" + dateStr + ".csv";
            case "behavior":
                return basePath + "\\user_behavior_summary\\user_behavior_summary_" + dateStr + ".csv";
            default:
                return basePath + "\\other_summary\\other_summary_" + dateStr + ".csv";
        }
    }
    
    // 获取ADS层数据文件路径
    public static String getAdsFilePath(String dataType) {
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        String basePath = "D:\\data_warehouse\\ads";
        
        switch (dataType.toLowerCase()) {
            case "rfm":
                return basePath + "\\rfm_analysis\\rfm_analysis_" + dateStr + ".csv";
            case "funnel":
                return basePath + "\\funnel_analysis\\funnel_analysis_" + dateStr + ".csv";
            case "sales":
                return basePath + "\\sales_report\\sales_report_" + dateStr + ".csv";
            case "portrait":
                return basePath + "\\user_portrait\\user_portrait_" + dateStr + ".csv";
            default:
                return basePath + "\\other_analysis\\other_analysis_" + dateStr + ".csv";
        }
    }
    
    // 验证数据映射配置
    public static boolean validateMappingConfig() {
        boolean isValid = true;
        
        // 检查用户字段映射
        if (USER_FIELD_MAPPING == null || USER_FIELD_MAPPING.length == 0) {
            System.err.println("用户字段映射配置为空");
            isValid = false;
        }
        
        // 检查商品字段映射
        if (GOODS_FIELD_MAPPING == null || GOODS_FIELD_MAPPING.length == 0) {
            System.err.println("商品字段映射配置为空");
            isValid = false;
        }
        
        // 检查订单字段映射
        if (ORDER_FIELD_MAPPING == null || ORDER_FIELD_MAPPING.length == 0) {
            System.err.println("订单字段映射配置为空");
            isValid = false;
        }
        
        return isValid;
    }
    
    // 生成映射配置报告
    public static void generateMappingReport() {
        System.out.println("========================================");
        System.out.println("数据映射配置报告");
        System.out.println("========================================");
        
        // 打印用户字段映射
        System.out.println("1. 用户字段映射：");
        for (String[] map : USER_FIELD_MAPPING) {
            System.out.printf("   %-15s -> %-15s\n", map[0], map[1]);
        }
        
        // 打印商品字段映射
        System.out.println("\n2. 商品字段映射：");
        for (String[] map : GOODS_FIELD_MAPPING) {
            System.out.printf("   %-15s -> %-15s\n", map[0], map[1]);
        }
        
        // 打印订单字段映射
        System.out.println("\n3. 订单字段映射：");
        for (String[] map : ORDER_FIELD_MAPPING) {
            System.out.printf("   %-15s -> %-15s\n", map[0], map[1]);
        }
        
        // 打印订单明细字段映射
        System.out.println("\n4. 订单明细字段映射：");
        for (String[] map : ORDER_DETAIL_FIELD_MAPPING) {
            System.out.printf("   %-15s -> %-15s\n", map[0], map[1]);
        }
        
        // 打印购物车字段映射
        System.out.println("\n5. 购物车字段映射：");
        for (String[] map : CART_FIELD_MAPPING) {
            System.out.printf("   %-15s -> %-15s\n", map[0], map[1]);
        }
        
        System.out.println("\n========================================");
        System.out.println("映射配置报告生成完成");
        System.out.println("========================================");
    }
    
    // 获取数据映射统计信息
    public static void getMappingStats() {
        System.out.println("数据映射统计信息：");
        System.out.println("========================");
        System.out.println("用户字段映射数量：" + USER_FIELD_MAPPING.length);
        System.out.println("商品字段映射数量：" + GOODS_FIELD_MAPPING.length);
        System.out.println("订单字段映射数量：" + ORDER_FIELD_MAPPING.length);
        System.out.println("订单明细字段映射数量：" + ORDER_DETAIL_FIELD_MAPPING.length);
        System.out.println("购物车字段映射数量：" + CART_FIELD_MAPPING.length);
        System.out.println("总映射字段数量：" + 
                (USER_FIELD_MAPPING.length + GOODS_FIELD_MAPPING.length + 
                 ORDER_FIELD_MAPPING.length + ORDER_DETAIL_FIELD_MAPPING.length + 
                 CART_FIELD_MAPPING.length));
        System.out.println("========================");
    }
}
