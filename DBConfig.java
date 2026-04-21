package com.qf.config;

public class DBConfig {
    // 数据库连接参数（根据实际环境修改）
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=Asia/Shanghai";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "123456";

    // 业务表名（订单/用户/商品）
    public static final String TABLE_ORDER = "t_order";
    public static final String TABLE_USER = "t_user";
    public static final String TABLE_PRODUCT = "t_product";
}
