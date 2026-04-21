package com.qf.service.impl;

import com.qf.dao.UserDao;
import com.qf.domain.User;
import com.qf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    // 校验用户名是否存在
    @Override
    public boolean checkName(String name) {
        return userDao.checkName(name) > 0;
    }

    // 校验游行是否存在
    @Override
    public boolean checkEmail(String email) {
        return userDao.checkEmail(email) > 0;
    }

    //注册
    @Override
    public boolean register(User user) {
        return userDao.register(user) > 0;
    }

    //登录（用户）
    @Override
    public User login(User user) {
        return userDao.login(user);
    }

    //登录（管理员）
    @Override
    public User adminLogin(User user) {
        return userDao.adminLogin(user);
    }

    //获取所有用户
    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    //删除用户
    @Override
    public int delUserById(int id) {
        return userDao.delUserById(id);
    }

    //用户条件查询
    @Override
    public List<User> getSearch(String username, String gender) {
        return userDao.getSearch(username,gender);
    }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }
    
    @Override
    public java.util.Map<String, Object> getUserInfoById(Integer userId) {
        java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
        
        try {
            User user = userDao.getUserById(userId);
            if (user != null) {
                userInfo.put("userId", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("gender", user.getGender());
                userInfo.put("role", user.getRole());
                userInfo.put("registerTime", user.getCreatetime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return userInfo;
    }
}
