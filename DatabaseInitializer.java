package com.qf.data;

import com.qf.data.vo.DataSourceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库初始化类，用于创建所需的表结构
 */
public class DatabaseInitializer {

    private final DataSourceConfig config;

    public DatabaseInitializer(DataSourceConfig config) {
        this.config = config;
    }

    /**
     * 初始化数据库表结构
     * @return 初始化结果
     */
    public boolean initialize() {
        System.out.println("开始初始化数据库表结构...");
        
        try (Connection conn = getConnection()) {
            // 1. 创建数据库（如果不存在）
            createDatabase(conn);
            
            // 2. 创建表结构
            createTables(conn);
            
            System.out.println("数据库表结构初始化完成！");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("数据库初始化失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 创建数据库（如果不存在）
     */
    private void createDatabase(Connection conn) throws Exception {
        // 从JDBC URL中提取数据库名
        String jdbcUrl = config.getJdbcUrl();
        String dbName = extractDatabaseName(jdbcUrl);
        
        if (dbName != null) {
            System.out.println("检查数据库 " + dbName + " 是否存在...");
            
            // 创建数据库的SQL语句
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            
            try (Statement stmt = conn.createStatement()) {
                // 连接到MySQL默认数据库
                String defaultJdbcUrl = jdbcUrl.substring(0, jdbcUrl.lastIndexOf("/")) + "/mysql";
                try (Connection defaultConn = DriverManager.getConnection(defaultJdbcUrl, config.getUsername(), config.getPassword())) {
                    try (Statement defaultStmt = defaultConn.createStatement()) {
                        defaultStmt.execute(createDbSql);
                        System.out.println("数据库 " + dbName + " 检查/创建完成");
                    }
                }
            } catch (Exception e) {
                System.out.println("数据库检查/创建失败（可能已存在）：" + e.getMessage());
                // 继续执行，不中断流程
            }
        }
    }

    /**
     * 从JDBC URL中提取数据库名
     */
    private String extractDatabaseName(String jdbcUrl) {
        if (jdbcUrl.contains("/") && jdbcUrl.contains("?")) {
            int start = jdbcUrl.lastIndexOf("/") + 1;
            int end = jdbcUrl.lastIndexOf("?");
            return jdbcUrl.substring(start, end);
        } else if (jdbcUrl.contains("/")) {
            int start = jdbcUrl.lastIndexOf("/") + 1;
            return jdbcUrl.substring(start);
        }
        return null;
    }

    /**
     * 创建表结构
     */
    private void createTables(Connection conn) throws Exception {
        // 先删除现有表（如果存在）
        dropExistingTables(conn);
        
        // 创建新表
        List<String> createTableSqls = getCreateTableSqls();
        
        try (Statement stmt = conn.createStatement()) {
            for (String sql : createTableSqls) {
                System.out.println("执行SQL：" + sql.substring(0, Math.min(sql.length(), 50)) + "...");
                stmt.executeUpdate(sql);
            }
        }
    }
    
    /**
     * 删除现有表（如果存在）
     */
    private void dropExistingTables(Connection conn) throws Exception {
        List<String> dropTableSqls = new ArrayList<>();
        
        // 按依赖关系顺序删除表
        dropTableSqls.add("DROP TABLE IF EXISTS t_order_item");
        dropTableSqls.add("DROP TABLE IF EXISTS t_orderdetail");
        dropTableSqls.add("DROP TABLE IF EXISTS t_order");
        dropTableSqls.add("DROP TABLE IF EXISTS t_cartdetail");
        dropTableSqls.add("DROP TABLE IF EXISTS t_cart");
        dropTableSqls.add("DROP TABLE IF EXISTS t_goods");
        dropTableSqls.add("DROP TABLE IF EXISTS t_goodstype");
        dropTableSqls.add("DROP TABLE IF EXISTS t_useraddress");
        dropTableSqls.add("DROP TABLE IF EXISTS t_category");
        dropTableSqls.add("DROP TABLE IF EXISTS t_user");
        
        try (Statement stmt = conn.createStatement()) {
            for (String sql : dropTableSqls) {
                System.out.println("执行SQL：" + sql);
                stmt.executeUpdate(sql);
            }
        }
    }

    /**
     * 获取创建表的SQL语句列表
     */
    private List<String> getCreateTableSqls() {
        List<String> sqls = new ArrayList<>();
        
        // 1. 用户表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_user` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
                "  `username` VARCHAR(50) NOT NULL COMMENT '用户名'," +
                "  `email` VARCHAR(100) NOT NULL COMMENT '邮箱'," +
                "  `gender` VARCHAR(10) COMMENT '性别'," +
                "  `create_time` DATETIME NOT NULL COMMENT '创建时间'," +
                "  `status` INT DEFAULT 1 COMMENT '状态：1-正常，0-禁用'," +
                "  INDEX idx_username (username)," +
                "  INDEX idx_email (email)," +
                "  INDEX idx_create_time (create_time)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'");
        
        // 2. 商品分类表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_category` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID'," +
                "  `name` VARCHAR(50) NOT NULL COMMENT '分类名称'," +
                "  `parent_id` INT DEFAULT 0 COMMENT '父分类ID'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表'");
        
        // 3. 商品表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_goods` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID'," +
                "  `name` VARCHAR(100) NOT NULL COMMENT '商品名称'," +
                "  `price` DOUBLE NOT NULL COMMENT '商品价格'," +
                "  `create_time` DATETIME NOT NULL COMMENT '创建时间'," +
                "  `category_id` INT COMMENT '分类ID'," +
                "  `description` TEXT COMMENT '商品描述'," +
                "  `image_url` VARCHAR(255) COMMENT '商品图片URL'," +
                "  `status` INT DEFAULT 1 COMMENT '状态：1-上架，0-下架'," +
                "  `rating` DOUBLE DEFAULT 0 COMMENT '商品评分'," +
                "  INDEX idx_name (name)," +
                "  INDEX idx_category_id (category_id)," +
                "  INDEX idx_status (status)," +
                "  INDEX idx_create_time (create_time)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表'");
        
        // 4. 订单表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_order` (" +
                "  `id` VARCHAR(32) PRIMARY KEY COMMENT '订单ID'," +
                "  `user_id` INT NOT NULL COMMENT '用户ID'," +
                "  `create_time` DATETIME NOT NULL COMMENT '订单创建时间'," +
                "  `total_amount` DOUBLE NOT NULL COMMENT '订单总金额'," +
                "  `status` INT DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消'," +
                "  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名'," +
                "  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话'," +
                "  `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货人地址'," +
                "  INDEX idx_user_id (user_id)," +
                "  INDEX idx_create_time (create_time)," +
                "  INDEX idx_status (status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表'");
        
        // 5. 订单项表（暂时移除外键约束，避免创建失败）
        sqls.add("CREATE TABLE IF NOT EXISTS `t_order_item` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID'," +
                "  `order_id` VARCHAR(32) NOT NULL COMMENT '订单ID'," +
                "  `product_id` INT NOT NULL COMMENT '商品ID'," +
                "  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称'," +
                "  `product_price` DOUBLE NOT NULL COMMENT '商品价格'," +
                "  `quantity` INT NOT NULL COMMENT '商品数量'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表'");
        
        // 6. 购物车表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_cart` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID'," +
                "  `user_id` INT NOT NULL COMMENT '用户ID'," +
                "  `product_id` INT NOT NULL COMMENT '商品ID'," +
                "  `quantity` INT NOT NULL COMMENT '商品数量'," +
                "  `status` INT DEFAULT 1 COMMENT '状态：1-有效，0-无效'," +
                "  INDEX idx_user_id (user_id)," +
                "  INDEX idx_product_id (product_id)," +
                "  INDEX idx_status (status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表'");
        
        // 7. 购物车详情表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_cartdetail` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车详情ID'," +
                "  `cart_id` INT NOT NULL COMMENT '购物车ID'," +
                "  `product_id` INT NOT NULL COMMENT '商品ID'," +
                "  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称'," +
                "  `product_price` DOUBLE NOT NULL COMMENT '商品价格'," +
                "  `quantity` INT NOT NULL COMMENT '商品数量'," +
                "  `create_time` DATETIME NOT NULL COMMENT '创建时间'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车详情表'");
        
        // 8. 商品类型表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_goodstype` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品类型ID'," +
                "  `name` VARCHAR(50) NOT NULL COMMENT '商品类型名称'," +
                "  `parent_id` INT DEFAULT 0 COMMENT '父类型ID'," +
                "  `status` INT DEFAULT 1 COMMENT '状态：1-有效，0-无效'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类型表'");
        
        // 9. 订单详情表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_orderdetail` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单详情ID'," +
                "  `order_id` VARCHAR(32) NOT NULL COMMENT '订单ID'," +
                "  `product_id` INT NOT NULL COMMENT '商品ID'," +
                "  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称'," +
                "  `product_price` DOUBLE NOT NULL COMMENT '商品价格'," +
                "  `quantity` INT NOT NULL COMMENT '商品数量'," +
                "  `total_price` DOUBLE NOT NULL COMMENT '总价'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表'");
        
        // 10. 用户地址表
        sqls.add("CREATE TABLE IF NOT EXISTS `t_useraddress` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID'," +
                "  `user_id` INT NOT NULL COMMENT '用户ID'," +
                "  `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名'," +
                "  `phone` VARCHAR(20) NOT NULL COMMENT '收货人电话'," +
                "  `province` VARCHAR(50) NOT NULL COMMENT '省份'," +
                "  `city` VARCHAR(50) NOT NULL COMMENT '城市'," +
                "  `district` VARCHAR(50) NOT NULL COMMENT '区县'," +
                "  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址'," +
                "  `is_default` INT DEFAULT 0 COMMENT '是否默认：1-是，0-否'," +
                "  `status` INT DEFAULT 1 COMMENT '状态：1-有效，0-无效'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表'");
        
        return sqls;
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                config.getJdbcUrl(),
                config.getUsername(),
                config.getPassword()
        );
    }
}
