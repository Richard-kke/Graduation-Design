package com.qf.controller;

import com.qf.common.utils.MD5Utils;
import com.qf.common.utils.RandomUtils;
import com.qf.domain.User;
import com.qf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    // 校验用户名是否存在
    @RequestMapping("checkname")
    @ResponseBody
    String checkName(String name){
        if(userService.checkName(name)){
            // 用户名已经存在
            return  "ERROR!!!";
        }else {
            return "SUCCESS";
        }
    }

    // 校验游行是否存在
    @RequestMapping("checkemail")
    @ResponseBody
    String checkEmail(String email){
        if(userService.checkEmail(email)){
            // 邮箱已经存在
            return  "ERROR!!!";
        }else {
            return "SUCCESS";
        }
    }

    //注册
    @RequestMapping("userregister")
    String register(User user, Model model){
        //密码加密
        user.setPassword(MD5Utils.md5(user.getPassword()));
        //激活码
        user.setActivatecode(RandomUtils.createActive());
        //
        if(userService.register(user)){ //注册成功
            return "login";
        }else { //注册失败
            model.addAttribute("registerMsg","注册失败,请稍核对后重试");
            return "register";
        }
    }

    //普通用户登录
    @RequestMapping("userlogin")
    String login(User user, Model model, HttpSession session){
        //密码加密
        user.setPassword(MD5Utils.md5(user.getPassword()));
        //
        User userlogin = userService.login(user);
        if (userlogin != null){ //登录成功
            session.setAttribute("user", userlogin);
            return "index";
        }else {
            model.addAttribute("loginMsg","用户名或密码错误");
            return "login";
        }
    }

    //管理员登录
    @RequestMapping("adminLogin")
    String adminLogin(User user, Model model, HttpSession session){
        //密码加密
        user.setPassword(MD5Utils.md5(user.getPassword()));
        //
        User userLogin = userService.adminLogin(user);
        if (userLogin != null){ //登录成功
            session.setAttribute("adminuser", userLogin);
            return "admin/admin";
        }else {
            model.addAttribute("loginMsg","用户名或密码错误");
            return "admin/login";
        }
    }

    //注销
    @RequestMapping("userloginout")
    String userloginout(String t,HttpSession session){
        if(t !=null){ //后台
            session.removeAttribute("adminuser");
            return "admin/login";
        }else { //前端用户
            session.removeAttribute("user");
            return "login";
        }
    }

    //获取所有用户
    @RequestMapping("userlist")
    @ResponseBody
    List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    //删除用户
    @RequestMapping("userdel")
    @ResponseBody
    int delUserById(int id){
        return userService.delUserById(id);
    }

    //用户条件查询
    @RequestMapping("usersearch")
    @ResponseBody
    List<User> getSearch(String username, String gender){
        return userService.getSearch(username,gender);
    }
}
