package com.qf.dao;

import com.qf.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {

    // 校验用户名是否存在
    @Select("select count(*) from t_user where username=#{username} ")
    int checkName(String name);

    // 校验游行是否存在
    @Select("select count(*) from t_user where email=#{email} ")
    int checkEmail(String email);

    //注册
    @Insert("insert into t_user(role,username,password,email,gender,createtime,flag,activatecode) " +  " values(1,#{username},#{password},#{email},#{gender},now(),2,#{activatecode})")
    int register(User user);

    //登录（用户）
    @Select("select * from t_user where flag=2 " +  " and username=#{username} and password=#{password}")
    @ResultType(User.class)
    User login(User user);

    //登录（管理员）
    @Select("select * from t_user where role=0 " +  " and username=#{username} and password=#{password}")
    @ResultType(User.class)
    User adminLogin(User user);

    //获取所有用户
    @Select("select * from t_user")
    @ResultType(User.class)
    List<User> getAllUsers();

    //删除用户
    @Delete("delete from t_user where id=#{id}")
    int delUserById(int id);

    //用户条件查询
    @Select("<script>" +
            "select * from t_user " +
            "<where>" +
            "<if test='gender!=null and gender.length()!=0'>" +
            " gender=#{gender} " +
            "</if>" +
            "<if test='username!=null and username.length()!=0'>" +
            " and username like concat('%',#{username},'%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<User> getSearch(@Param("username") String username, @Param("gender") String gender);
    
    // 更新用户信息
    @Update("update t_user set username=#{username}, email=#{email}, gender=#{gender}, password=#{password} where id=#{id}")
    int updateUser(User user);
    
    // 根据ID获取用户
    @Select("select * from t_user where id=#{id}")
    @ResultType(User.class)
    User getUserById(int id);
}

