package com.qf.domain;

public class Order {
    private String id;  // 订单ID，字符串类型
    private int uaid;   // 用户地址ID
    private int uid;    // 用户ID
    private String createtime;  // 创建时间，datetime类型
    private long money;  // 订单金额，bigint类型
    private int flag;   // 订单状态标志

    public Order() {
    }

    public Order(String id, int uaid, int uid, String createtime, long money, int flag) {
        this.id = id;
        this.uaid = uaid;
        this.uid = uid;
        this.createtime = createtime;
        this.money = money;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUaid() {
        return uaid;
    }

    public void setUaid(int uaid) {
        this.uaid = uaid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", uaid=" + uaid +
                ", uid=" + uid +
                ", createtime='" + createtime + '\'' +
                ", money=" + money +
                ", flag=" + flag +
                '}';
    }
}