package com.qf.domain;

public class User {
    private int id;
    private int role;  // 角色类型 0管理员 1会员
    private String username;
    private String password;
    private String email;
    private String gender;
    private String createtime;  // 数据库类型：datetime
    private int flag;  // 标记位 1未激活 2有效 3临时无效 4永久无效
    private String activatecode;  // 后台随机生成

    public User() {
    }

    public User(String username, String password, String email, String gender, String activatecode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.activatecode = activatecode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getActivatecode() {
        return activatecode;
    }

    public void setActivatecode(String activatecode) {
        this.activatecode = activatecode;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", createtime='" + createtime + '\'' +
                ", flag=" + flag +
                '}';
    }
}
