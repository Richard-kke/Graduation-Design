package com.qf.domain;

public class CartDetail {
    private int id;         // 购物车详情ID
    private int cartId;     // 购物车ID
    private int goodsId;    // 商品ID
    private String goodsName; // 商品名称
    private long goodsPrice; // 商品单价
    private int goodsNum;   // 商品数量
    private long totalPrice; // 商品总价

    public CartDetail() {
    }

    public CartDetail(int cartId, int goodsId, String goodsName, long goodsPrice, int goodsNum) {
        this.cartId = cartId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsNum = goodsNum;
        this.totalPrice = goodsPrice * goodsNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public long getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(long goodsPrice) {
        this.goodsPrice = goodsPrice;
        this.totalPrice = goodsPrice * this.goodsNum;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
        this.totalPrice = this.goodsPrice * goodsNum;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "CartDetail{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", goodsNum=" + goodsNum +
                ", totalPrice=" + totalPrice +
                '}';
    }
}