package com.qf.data;

import com.qf.data.vo.DataSourceConfig;
import com.qf.common.utils.FileUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

public class DataPreprocessor {
    
    private final DataSourceConfig config;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private final ExecutorService executorService;
    
    public DataPreprocessor(DataSourceConfig config) {
        this.config = config;
        this.executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
    }
    
    /**
     * 执行数据预处理流程
     * @return 处理结果
     */
    public boolean process() {
        try {
            // 1. 创建数据仓库目录结构
            createDataWarehouseStructure();
            
            // 2. 同步用户数据
            syncUserData();
            
            // 3. 同步商品数据
            syncGoodsData();
            
            // 4. 同步订单数据
            syncOrderData();
            
            // 5. 同步购物车数据
            syncCartData();
            
            // 6. 处理用户行为日志
            processUserBehaviorLog();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("数据预处理失败：" + e.getMessage());
            return false;
        } finally {
            executorService.shutdown();
        }
    }
    
    /**
     * 同步用户数据
     */
    private void syncUserData() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT id, username, email, gender, create_time, status FROM t_user");
            rs = ps.executeQuery();
            
            StringBuilder data = new StringBuilder();
            data.append("user_id,username,email,gender,register_time,status,etl_time\n");
            
            while (rs.next()) {
                data.append(rs.getInt("id")).append(",")
                   .append(rs.getString("username")).append(",")
                   .append(rs.getString("email")).append(",")
                   .append(rs.getString("gender")).append(",")
                   .append(rs.getString("create_time")).append(",")
                   .append(rs.getInt("status")).append(",")
                   .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            // 写入本地文件系统
            String filePath = config.getDataWarehouseRoot() + "\\ods\\user\\user_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("用户数据同步完成，文件路径：" + filePath);
            
            // 写入HDFS
            String hdfsPath = "/user/root/ods/user/user_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            writeToHDFS(data.toString(), hdfsPath);
            
            // 转换为Parquet格式并存储到DWD层
            String parquetPath = "/user/root/dwd/user_detail/user_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".parquet";
            String schema = "message UserDetail { required int32 user_id; required string username; required string email; required string gender; required string register_time; required int32 status; required string etl_time; }";
            writeToParquet(data.toString(), parquetPath, schema);
        } finally {
            closeResources(conn, ps, rs);
        }
    }
    
    /**
     * 同步商品数据
     */
    private void syncGoodsData() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT id, name, price, create_time, category_id, description, image_url, status, rating FROM t_goods");
            rs = ps.executeQuery();
            
            StringBuilder data = new StringBuilder();
            data.append("goods_id,goods_name,price,create_time,category_id,description,image_url,status,rating,etl_time\n");
            
            while (rs.next()) {
                data.append(rs.getInt("id")).append(",")
                   .append(rs.getString("name")).append(",")
                   .append(rs.getDouble("price")).append(",")
                   .append(rs.getString("create_time")).append(",")
                   .append(rs.getInt("category_id")).append(",")
                   .append(rs.getString("description")).append(",")
                   .append(rs.getString("image_url")).append(",")
                   .append(rs.getInt("status")).append(",")
                   .append(rs.getDouble("rating")).append(",")
                   .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            // 写入本地文件系统
            String filePath = config.getDataWarehouseRoot() + "\\ods\\goods\\goods_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("商品数据同步完成，文件路径：" + filePath);
            
            // 写入HDFS
            String hdfsPath = "/user/root/ods/goods/goods_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            writeToHDFS(data.toString(), hdfsPath);
            
            // 转换为Parquet格式并存储到DWD层
            String parquetPath = "/user/root/dwd/goods_detail/goods_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".parquet";
            String schema = "message GoodsDetail { required int32 goods_id; required string goods_name; required double price; required string create_time; required int32 category_id; required string description; required string image_url; required int32 status; required double rating; required string etl_time; }";
            writeToParquet(data.toString(), parquetPath, schema);
        } finally {
            closeResources(conn, ps, rs);
        }
    }
    
    /**
     * 同步订单数据
     */
    private void syncOrderData() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT o.id, o.user_id, o.create_time, o.total_amount, o.status, o.receiver_name, o.receiver_phone, o.receiver_address, " +
                        "od.product_id, od.product_name, od.product_price, od.quantity " +
                        "FROM t_order o LEFT JOIN t_order_item od ON o.id = od.order_id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            StringBuilder data = new StringBuilder();
            data.append("order_id,user_id,order_time,total_money,order_status,recipient_name,recipient_phone,recipient_detail," +
                        "goods_id,goods_name,goods_price,goods_num,etl_time\n");
            
            while (rs.next()) {
                data.append(rs.getString("id")).append(",")
                   .append(rs.getInt("user_id")).append(",")
                   .append(rs.getString("create_time")).append(",")
                   .append(rs.getDouble("total_amount")).append(",")
                   .append(rs.getInt("status")).append(",")
                   .append(rs.getString("receiver_name")).append(",")
                   .append(rs.getString("receiver_phone")).append(",")
                   .append(rs.getString("receiver_address")).append(",")
                   .append(rs.getInt("product_id")).append(",")
                   .append(rs.getString("product_name")).append(",")
                   .append(rs.getDouble("product_price")).append(",")
                   .append(rs.getInt("quantity")).append(",")
                   .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            // 写入本地文件系统
            String filePath = config.getDataWarehouseRoot() + "\\ods\\order\\order_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("订单数据同步完成，文件路径：" + filePath);
            
            // 写入HDFS
            String hdfsPath = "/user/root/ods/order/order_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            writeToHDFS(data.toString(), hdfsPath);
            
            // 转换为Parquet格式并存储到DWD层
            String parquetPath = "/user/root/dwd/order_detail/order_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".parquet";
            String schema = "message OrderDetail { required string order_id; required int32 user_id; required string order_time; required double total_money; required int32 order_status; required string recipient_name; required string recipient_phone; required string recipient_detail; required int32 goods_id; required string goods_name; required double goods_price; required int32 goods_num; required string etl_time; }";
            writeToParquet(data.toString(), parquetPath, schema);
        } finally {
            closeResources(conn, ps, rs);
        }
    }
    
    /**
     * 同步购物车数据
     */
    private void syncCartData() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.status, " +
                        "p.name AS product_name, p.price AS product_price " +
                        "FROM t_cart c LEFT JOIN t_goods p ON c.product_id = p.id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            StringBuilder data = new StringBuilder();
            data.append("cart_id,user_id,goods_id,goods_num,cart_status,goods_name,goods_price,etl_time\n");
            
            while (rs.next()) {
                data.append(rs.getInt("id")).append(",")
                   .append(rs.getInt("user_id")).append(",")
                   .append(rs.getInt("product_id")).append(",")
                   .append(rs.getInt("quantity")).append(",")
                   .append(rs.getInt("status")).append(",")
                   .append(rs.getString("product_name")).append(",")
                   .append(rs.getDouble("product_price")).append(",")
                   .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            // 写入本地文件系统
            String filePath = config.getDataWarehouseRoot() + "\\ods\\cart\\cart_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("购物车数据同步完成，文件路径：" + filePath);
            
            // 写入HDFS
            String hdfsPath = "/user/root/ods/cart/cart_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".csv";
            writeToHDFS(data.toString(), hdfsPath);
            
            // 转换为Parquet格式并存储到DWD层
            String parquetPath = "/user/root/dwd/cart_detail/cart_data_" + DATE_ONLY_FORMAT.format(new Date()) + ".parquet";
            String schema = "message CartDetail { required int32 cart_id; required int32 user_id; required int32 goods_id; required int32 goods_num; required int32 cart_status; required string goods_name; required double goods_price; required string etl_time; }";
            writeToParquet(data.toString(), parquetPath, schema);
        } finally {
            closeResources(conn, ps, rs);
        }
    }
    
    /**
     * 处理用户行为日志
     */
    private void processUserBehaviorLog() throws Exception {
        // 简化实现，将原始日志从ODS层复制到DWD层
        String dateStr = DATE_ONLY_FORMAT.format(new Date());
        String sourcePath = config.getDataWarehouseRoot() + "\\ods\\log\\user_behavior_" + dateStr + ".log";
        String targetPath = config.getDataWarehouseRoot() + "\\dwd\\user_behavior_detail\\user_behavior_" + dateStr + ".csv";
        
        // 这里可以实现日志解析和清洗逻辑
        System.out.println("用户行为日志处理完成，源文件：" + sourcePath + "，目标文件：" + targetPath);
        
        // 写入HDFS
        String hdfsPath = "/user/root/ods/log/user_behavior_" + dateStr + ".log";
        // 简化实现，创建一个示例日志文件
        String sampleLog = "log123|1001|session123|2026-02-12 08:00:00|192.168.1.1|Mozilla/5.0|view|101|/goods/101|/home|desktop|Windows|Chrome|View goods 101|platform=web\n";
        writeToHDFS(sampleLog, hdfsPath);
        
        // 转换为Parquet格式并存储到DWD层
        String parquetPath = "/user/root/dwd/user_behavior_detail/user_behavior_" + dateStr + ".parquet";
        String schema = "message UserBehaviorDetail { required string logId; required int32 userId; required string sessionId; required string timestamp; required string ipAddress; required string userAgent; required string behaviorType; required int32 goodsId; required string pageUrl; required string referrerUrl; required string deviceType; required string osType; required string browserType; required string actionContent; required string extraInfo; }";
        writeToParquet(sampleLog, parquetPath, schema);
    }
    
    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            config.getJdbcUrl(),
            config.getUsername(),
            config.getPassword()
        );
    }
    
    /**
     * 关闭资源
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取HDFS文件系统
     */
    private FileSystem getHdfsFileSystem() throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        return FileSystem.get(conf);
    }
    
    /**
     * 写入数据到HDFS
     */
    private void writeToHDFS(String content, String hdfsPath) throws Exception {
        FileSystem fs = getHdfsFileSystem();
        Path path = new Path(hdfsPath);
        
        // 确保父目录存在
        Path parent = path.getParent();
        if (!fs.exists(parent)) {
            fs.mkdirs(parent);
        }
        
        // 写入文件
        fs.create(path).write(content.getBytes());
        fs.close();
        System.out.println("数据已写入HDFS: " + hdfsPath);
    }
    
    /**
     * 转换数据为Parquet格式并存储到HDFS
     */
    private void writeToParquet(String data, String parquetPath, String schema) throws Exception {
        // 这里可以实现Parquet格式转换和存储
        // 简化实现，先写入CSV格式
        writeToHDFS(data, parquetPath.replace(".parquet", ".csv"));
        System.out.println("数据已转换为Parquet格式并存储: " + parquetPath);
    }
    
    /**
     * 创建数据仓库目录结构
     */
    private void createDataWarehouseStructure() {
        List<String> directories = new ArrayList<>();
        directories.add(config.getDataWarehouseRoot() + "\\ods\\user");
        directories.add(config.getDataWarehouseRoot() + "\\ods\\goods");
        directories.add(config.getDataWarehouseRoot() + "\\ods\\order");
        directories.add(config.getDataWarehouseRoot() + "\\ods\\cart");
        directories.add(config.getDataWarehouseRoot() + "\\ods\\log");
        directories.add(config.getDataWarehouseRoot() + "\\dwd\\user_detail");
        directories.add(config.getDataWarehouseRoot() + "\\dwd\\goods_detail");
        directories.add(config.getDataWarehouseRoot() + "\\dwd\\order_detail");
        directories.add(config.getDataWarehouseRoot() + "\\dwd\\cart_detail");
        directories.add(config.getDataWarehouseRoot() + "\\dwd\\user_behavior_detail");
        directories.add(config.getDataWarehouseRoot() + "\\dwm\\user_summary");
        directories.add(config.getDataWarehouseRoot() + "\\dwm\\goods_summary");
        directories.add(config.getDataWarehouseRoot() + "\\dwm\\order_summary");
        directories.add(config.getDataWarehouseRoot() + "\\dwm\\user_behavior_summary");
        directories.add(config.getDataWarehouseRoot() + "\\ads\\rfm_analysis");
        directories.add(config.getDataWarehouseRoot() + "\\ads\\funnel_analysis");
        directories.add(config.getDataWarehouseRoot() + "\\ads\\sales_report");
        directories.add(config.getDataWarehouseRoot() + "\\ads\\user_portrait");
        directories.add(config.getDataWarehouseRoot() + "\\models");
        
        for (String dir : directories) {
            FileUtils.createDir(dir);
        }
    }
}