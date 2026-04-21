package com.qf.domain;

public class Cart {
    private int id;     // 购物车ID
    private int uid;    // 用户ID
    private long money; // 购物车总金额，bigint类型

    public Cart() {
    }

    public Cart(int uid, long money) {
        this.uid = uid;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", uid=" + uid +
                ", money=" + money +
                '}';
    }
}