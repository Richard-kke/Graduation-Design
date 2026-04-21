package com.qf.service;

import com.qf.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserService {

    // 校验用户名是否存在
    boolean checkName(String name);
    // 校验游行是否存在
    boolean checkEmail(String email);
    //注册
    boolean register(User user);
    //登录（用户）
    User login(User user);
    //登录（管理员）
    User adminLogin(User user);
    //获取所有用户
    List<User> getAllUsers();
    //删除用户
    int delUserById(int id);
    //用户条件查询
    List<User> getSearch(String username, String gender);
    
    // 更新用户信息
    int updateUser(User user);
    
    // 根据ID获取用户
    User getUserById(int id);
    
    // 根据ID获取用户信息（Map格式）
    java.util.Map<String, Object> getUserInfoById(Integer userId);
}