package com.qf.common.utils;

import com.qf.domain.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据映射验证工具类
 * 用于验证实体类与数据库表的映射关系
 */
public class DataMappingValidator {
    
    /**
     * 验证所有实体类的映射关系
     * @return 验证结果
     */
    public static Map<String, Boolean> validateAllMappings() {
        Map<String, Boolean> results = new HashMap<>();
        
        results.put("User", validateUserMapping());
        results.put("Goods", validateGoodsMapping());
        results.put("GoodsType", validateGoodsTypeMapping());
        results.put("Order", validateOrderMapping());
        results.put("Cart", validateCartMapping());
        results.put("CartDetail", validateCartDetailMapping());
        results.put("OrderDetail", validateOrderDetailMapping());
        results.put("UserAddress", validateUserAddressMapping());
        
        return results;
    }
    
    /**
     * 验证用户实体映射
     * @return 验证结果
     */
    public static boolean validateUserMapping() {
        try {
            User user = new User();
            Field[] fields = user.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "role", "username", "password", "email", "gender", "createtime", "flag", "activatecode"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("User实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            // 检查字段类型是否匹配
            // id 应该是 int 类型
            Field idField = user.getClass().getDeclaredField("id");
            if (!idField.getType().equals(int.class)) {
                System.err.println("User.id 字段类型错误，应该是 int");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证商品实体映射
     * @return 验证结果
     */
    public static boolean validateGoodsMapping() {
        try {
            Goods goods = new Goods();
            Field[] fields = goods.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "name", "price", "pubdate", "typeName", "intro", "picture", "flag", "star"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("Goods实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            // 检查字段类型是否匹配
            // price 应该是 long 类型
            Field priceField = goods.getClass().getDeclaredField("price");
            if (!priceField.getType().equals(long.class)) {
                System.err.println("Goods.price 字段类型错误，应该是 long");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证商品类型实体映射
     * @return 验证结果
     */
    public static boolean validateGoodsTypeMapping() {
        try {
            GoodsType goodsType = new GoodsType();
            Field[] fields = goodsType.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "name", "level", "parentName", "flag"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("GoodsType实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证订单实体映射
     * @return 验证结果
     */
    public static boolean validateOrderMapping() {
        try {
            Order order = new Order();
            Field[] fields = order.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "uaid", "uid", "createtime", "money", "flag"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("Order实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证购物车实体映射
     * @return 验证结果
     */
    public static boolean validateCartMapping() {
        try {
            Cart cart = new Cart();
            Field[] fields = cart.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "uid", "money"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("Cart实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证购物车详情实体映射
     * @return 验证结果
     */
    public static boolean validateCartDetailMapping() {
        try {
            CartDetail cartDetail = new CartDetail();
            Field[] fields = cartDetail.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "cartId", "goodsId", "goodsName", "goodsPrice", "goodsNum", "totalPrice"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("CartDetail实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证订单详情实体映射
     * @return 验证结果
     */
    public static boolean validateOrderDetailMapping() {
        try {
            OrderDetail orderDetail = new OrderDetail();
            Field[] fields = orderDetail.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "orderId", "goodsId", "goodsName", "goodsPrice", "goodsNum", "totalPrice"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("OrderDetail实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证用户地址实体映射
     * @return 验证结果
     */
    public static boolean validateUserAddressMapping() {
        try {
            UserAddress userAddress = new UserAddress();
            Field[] fields = userAddress.getClass().getDeclaredFields();
            
            // 检查核心字段是否存在
            String[] requiredFields = {"id", "userId", "name", "phone", "province", "city", "district", "detail", "isDefault"};
            for (String fieldName : requiredFields) {
                boolean found = false;
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.err.println("UserAddress实体缺少字段: " + fieldName);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 打印映射验证结果
     * @param results 验证结果
     */
    public static void printValidationResults(Map<String, Boolean> results) {
        System.out.println("========================================");
        System.out.println("数据映射验证结果");
        System.out.println("========================================");
        
        boolean allValid = true;
        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            String status = entry.getValue() ? "✓ 有效" : "✗ 无效";
            System.out.printf("%s: %s\n", entry.getKey(), status);
            if (!entry.getValue()) {
                allValid = false;
            }
        }
        
        System.out.println("========================================");
        System.out.println("总体结果: " + (allValid ? "✓ 所有映射有效" : "✗ 存在无效映射") + "");
        System.out.println("========================================");
    }
    
    /**
     * 数据验证示例
     */
    public static void main(String[] args) {
        Map<String, Boolean> results = validateAllMappings();
        printValidationResults(results);
    }
}