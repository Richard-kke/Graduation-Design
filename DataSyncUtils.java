package com.qf.common.utils;

import com.qf.config.DBConfig;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSyncUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    // 同步用户数据到数据仓库
    public static void syncUserData() {
        Connection sourceConn = null;
        Connection targetConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 获取源数据库连接（MySQL业务库）
            sourceConn = DbUtils.getConnection();
            
            // 查询用户数据
            String sql = "SELECT id, username, email, gender, create_time AS createtime, status AS flag FROM " + DBConfig.TABLE_USER;
            ps = sourceConn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // 构建用户数据文件路径（ODS层）
            String dateStr = DATE_ONLY_FORMAT.format(new Date());
            String filePath = "D:\\data_warehouse\\ods\\user\\user_data_" + dateStr + ".csv";
            
            // 写入数据到CSV文件
            StringBuilder data = new StringBuilder();
            data.append("id,role,username,email,gender,createtime,flag,etl_time\n");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                String createtime = rs.getString("createtime");
                int flag = rs.getInt("flag");
                
                data.append(id).append(",")
                    .append(0).append(",") // 默认角色为0（普通用户）
                    .append(username).append(",")
                    .append(email).append(",")
                    .append(gender).append(",")
                    .append(createtime).append(",")
                    .append(flag).append(",")
                    .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("User data sync completed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeConnection(sourceConn, ps, rs);
        }
    }
    
    // 同步商品数据到数据仓库
    public static void syncGoodsData() {
        Connection sourceConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 获取源数据库连接
            sourceConn = DbUtils.getConnection();
            
            // 查询商品数据
            String sql = "SELECT id, name, price, create_time AS pubdate, category_id AS typeName, description AS intro, image_url AS picture, status AS flag, rating AS star FROM " + DBConfig.TABLE_PRODUCT;
            ps = sourceConn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // 构建商品数据文件路径（ODS层）
            String dateStr = DATE_ONLY_FORMAT.format(new Date());
            String filePath = "D:\\data_warehouse\\ods\\goods\\goods_data_" + dateStr + ".csv";
            
            // 写入数据到CSV文件
            StringBuilder data = new StringBuilder();
            data.append("id,name,price,pubdate,typeName,intro,picture,flag,star,etl_time\n");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String price = rs.getString("price");
                String pubdate = rs.getString("pubdate");
                String typeName = rs.getString("typeName");
                String intro = rs.getString("intro");
                String picture = rs.getString("picture");
                int flag = rs.getInt("flag");
                int star = rs.getInt("star");
                
                data.append(id).append(",")
                    .append(name).append(",")
                    .append(price).append(",")
                    .append(pubdate).append(",")
                    .append(typeName).append(",")
                    .append(intro).append(",")
                    .append(picture).append(",")
                    .append(flag).append(",")
                    .append(star).append(",")
                    .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("Goods data sync completed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeConnection(sourceConn, ps, rs);
        }
    }
    
    // 同步订单数据到数据仓库
    public static void syncOrderData() {
        Connection sourceConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 获取源数据库连接
            sourceConn = DbUtils.getConnection();
            
            // 查询订单数据
            String sql = "SELECT o.id, o.user_id, o.create_time AS createtime, o.total_amount AS money, o.status AS flag, o.receiver_name AS name, o.receiver_phone AS phone, o.receiver_address AS detail, " +
                        "od.product_id AS goods_id, od.product_name AS goods_name, od.product_price AS goods_price, od.quantity AS goods_num " +
                        "FROM " + DBConfig.TABLE_ORDER + " o LEFT JOIN order_item od ON o.id = od.order_id";
            ps = sourceConn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // 构建订单数据文件路径（ODS层）
            String dateStr = DATE_ONLY_FORMAT.format(new Date());
            String filePath = "D:\\data_warehouse\\ods\\order\\order_data_" + dateStr + ".csv";
            
            // 写入数据到CSV文件
            StringBuilder data = new StringBuilder();
            data.append("order_id,user_id,order_time,total_money,order_status,recipient_name,recipient_phone,recipient_detail," +
                        "goods_id,goods_name,goods_price,goods_num,etl_time\n");
            
            while (rs.next()) {
                String orderId = rs.getString("id");
                int userId = rs.getInt("user_id");
                String orderTime = rs.getString("createtime");
                double totalMoney = rs.getDouble("money");
                int orderStatus = rs.getInt("flag");
                String recipientName = rs.getString("name");
                String recipientPhone = rs.getString("phone");
                String recipientDetail = rs.getString("detail");
                int goodsId = rs.getInt("goods_id");
                String goodsName = rs.getString("goods_name");
                String goodsPrice = rs.getString("goods_price");
                int goodsNum = rs.getInt("goods_num");
                
                data.append(orderId).append(",")
                    .append(userId).append(",")
                    .append(orderTime).append(",")
                    .append(totalMoney).append(",")
                    .append(orderStatus).append(",")
                    .append(recipientName).append(",")
                    .append(recipientPhone).append(",")
                    .append(recipientDetail).append(",")
                    .append(goodsId).append(",")
                    .append(goodsName).append(",")
                    .append(goodsPrice).append(",")
                    .append(goodsNum).append(",")
                    .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("Order data sync completed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeConnection(sourceConn, ps, rs);
        }
    }
    
    // 同步购物车数据到数据仓库
    public static void syncCartData() {
        Connection sourceConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 获取源数据库连接
            sourceConn = DbUtils.getConnection();
            
            // 查询购物车数据
            String sql = "SELECT c.id, c.user_id, c.product_id AS goods_id, c.quantity AS goods_num, c.status AS flag, " +
                        "p.name AS goods_name, p.price AS goods_price " +
                        "FROM cart c LEFT JOIN " + DBConfig.TABLE_PRODUCT + " p ON c.product_id = p.id";
            ps = sourceConn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // 构建购物车数据文件路径（ODS层）
            String dateStr = DATE_ONLY_FORMAT.format(new Date());
            String filePath = "D:\\data_warehouse\\ods\\cart\\cart_data_" + dateStr + ".csv";
            
            // 写入数据到CSV文件
            StringBuilder data = new StringBuilder();
            data.append("cart_id,user_id,goods_id,goods_num,cart_status,goods_name,goods_price,etl_time\n");
            
            while (rs.next()) {
                int cartId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int goodsId = rs.getInt("goods_id");
                int goodsNum = rs.getInt("goods_num");
                int cartStatus = rs.getInt("flag");
                String goodsName = rs.getString("goods_name");
                String goodsPrice = rs.getString("goods_price");
                
                data.append(cartId).append(",")
                    .append(userId).append(",")
                    .append(goodsId).append(",")
                    .append(goodsNum).append(",")
                    .append(cartStatus).append(",")
                    .append(goodsName).append(",")
                    .append(goodsPrice).append(",")
                    .append(DATE_FORMAT.format(new Date())).append("\n");
            }
            
            FileUtils.writeFile(filePath, data.toString(), false);
            System.out.println("Cart data sync completed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeConnection(sourceConn, ps, rs);
        }
    }
    
    // 执行全量数据同步
    public static void executeFullSync() {
        System.out.println("Starting full data synchronization at " + DATE_FORMAT.format(new Date()));
        
        // 同步用户数据
        syncUserData();
        
        // 同步商品数据
        syncGoodsData();
        
        // 同步订单数据
        syncOrderData();
        
        // 同步购物车数据
        syncCartData();
        
        System.out.println("Full data synchronization completed at " + DATE_FORMAT.format(new Date()));
    }
    
    // 执行增量数据同步
    public static void executeIncrementalSync(String lastSyncTime) {
        System.out.println("Starting incremental data synchronization at " + DATE_FORMAT.format(new Date()));
        System.out.println("Last sync time: " + lastSyncTime);
        
        // 这里可以实现增量同步逻辑，基于上次同步时间
        // 目前简化实现，仅记录日志
        
        System.out.println("Incremental data synchronization completed at " + DATE_FORMAT.format(new Date()));
    }
    
    // 获取数据仓库目录结构
    public static List<String> getDataWarehouseStructure() {
        List<String> structure = new ArrayList<>();
        structure.add("数据仓库目录结构：");
        structure.add("├── D:\\data_warehouse"); // 去掉末尾的反斜杠，避免转义问题
        structure.add("│   ├── ods/           # 原始数据层（Operational Data Store）");
        structure.add("│   │   ├── user/      # 用户原始数据");
        structure.add("│   │   ├── goods/     # 商品原始数据");
        structure.add("│   │   ├── order/     # 订单原始数据");
        structure.add("│   │   ├── cart/      # 购物车原始数据");
        structure.add("│   │   └── log/       # 用户行为日志原始数据");
        structure.add("│   ├── dwd/           # 明细数据层（Data Warehouse Detail）");
        structure.add("│   │   ├── user_detail/      # 用户明细数据");
        structure.add("│   │   ├── goods_detail/     # 商品明细数据");
        structure.add("│   │   ├── order_detail/     # 订单明细数据");
        structure.add("│   │   ├── cart_detail/      # 购物车明细数据");
        structure.add("│   │   └── user_behavior_detail/  # 用户行为明细数据");
        structure.add("│   ├── dwm/           # 汇总数据层（Data Warehouse Middle）");
        structure.add("│   │   ├── user_summary/      # 用户汇总数据");
        structure.add("│   │   ├── goods_summary/     # 商品汇总数据");
        structure.add("│   │   ├── order_summary/     # 订单汇总数据");
        structure.add("│   │   └── user_behavior_summary/  # 用户行为汇总数据");
        structure.add("│   └── ads/           # 应用数据层（Application Data Store）");
        structure.add("│       ├── rfm_analysis/      # RFM分析结果");
        structure.add("│       ├── funnel_analysis/    # 漏斗分析结果");
        structure.add("│       ├── sales_report/       # 销售报表");
        structure.add("│       └── user_portrait/      # 用户画像");
        return structure;
    }
}