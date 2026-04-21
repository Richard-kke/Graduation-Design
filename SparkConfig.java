package com.qf.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

public class SparkConfig {
    
    // Spark应用名称
    public static final String APP_NAME = "EcommerceAnalytics";
    
    // Spark Master地址
    public static final String MASTER = "local[*]";
    
    // 数据仓库根目录
    public static final String DATA_WAREHOUSE_ROOT = "D:\\data_warehouse";
    
    // ODS层数据路径
    public static final String ODS_USER_PATH = DATA_WAREHOUSE_ROOT + "\\ods\\user";
    public static final String ODS_GOODS_PATH = DATA_WAREHOUSE_ROOT + "\\ods\\goods";
    public static final String ODS_ORDER_PATH = DATA_WAREHOUSE_ROOT + "\\ods\\order";
    public static final String ODS_CART_PATH = DATA_WAREHOUSE_ROOT + "\\ods\\cart";
    public static final String ODS_LOG_PATH = DATA_WAREHOUSE_ROOT + "\\ods\\log";
    
    // DWD层数据路径
    public static final String DWD_USER_DETAIL_PATH = DATA_WAREHOUSE_ROOT + "\\dwd\\user_detail";
    public static final String DWD_GOODS_DETAIL_PATH = DATA_WAREHOUSE_ROOT + "\\dwd\\goods_detail";
    public static final String DWD_ORDER_DETAIL_PATH = DATA_WAREHOUSE_ROOT + "\\dwd\\order_detail";
    public static final String DWD_CART_DETAIL_PATH = DATA_WAREHOUSE_ROOT + "\\dwd\\cart_detail";
    public static final String DWD_USER_BEHAVIOR_PATH = DATA_WAREHOUSE_ROOT + "\\dwd\\user_behavior_detail";
    
    // DWM层数据路径
    public static final String DWM_USER_SUMMARY_PATH = DATA_WAREHOUSE_ROOT + "\\dwm\\user_summary";
    public static final String DWM_GOODS_SUMMARY_PATH = DATA_WAREHOUSE_ROOT + "\\dwm\\goods_summary";
    public static final String DWM_ORDER_SUMMARY_PATH = DATA_WAREHOUSE_ROOT + "\\dwm\\order_summary";
    public static final String DWM_USER_BEHAVIOR_SUMMARY_PATH = DATA_WAREHOUSE_ROOT + "\\dwm\\user_behavior_summary";
    
    // ADS层数据路径
    public static final String ADS_RFM_ANALYSIS_PATH = DATA_WAREHOUSE_ROOT + "\\ads\\rfm_analysis";
    public static final String ADS_FUNNEL_ANALYSIS_PATH = DATA_WAREHOUSE_ROOT + "\\ads\\funnel_analysis";
    public static final String ADS_SALES_REPORT_PATH = DATA_WAREHOUSE_ROOT + "\\ads\\sales_report";
    public static final String ADS_USER_PORTRAIT_PATH = DATA_WAREHOUSE_ROOT + "\\ads\\user_portrait";
    
    // 推荐模型相关路径
    public static final String RECOMMENDATION_MODEL_PATH = DATA_WAREHOUSE_ROOT + "\\models\\recommendation";
    public static final String USER_PROFILE_MODEL_PATH = DATA_WAREHOUSE_ROOT + "\\models\\user_profile";
    
    // 创建Spark配置
    public static SparkConf createSparkConf() {
        SparkConf conf = new SparkConf()
                .setAppName(APP_NAME)
                .setMaster(MASTER)
                .set("spark.driver.memory", "2g")
                .set("spark.executor.memory", "4g")
                .set("spark.sql.warehouse.dir", DATA_WAREHOUSE_ROOT + "\\spark-warehouse");
        
        return conf;
    }
    
    // 创建JavaSparkContext
    public static JavaSparkContext createJavaSparkContext() {
        SparkConf conf = createSparkConf();
        return new JavaSparkContext(conf);
    }
    
    // 创建SQLContext
    public static SQLContext createSQLContext(JavaSparkContext sc) {
        return new SQLContext(sc);
    }
}
