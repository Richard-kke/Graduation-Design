package com.qf.domain;

public class Goods {
    private int id;
    private String name;
    private long price;  // 数据库类型：bigint(20)
    private String pubdate;  // 数据库类型：date
    private String typeName;
    private String intro;
    private String picture;
    private int flag;
    private int star;

    public Goods() {
    }

    public Goods(String name, long price, String pubdate, String typeName, String intro, String picture, int flag, int star) {
        this.name = name;
        this.price = price;
        this.pubdate = pubdate;
        this.typeName = typeName;
        this.intro = intro;
        this.picture = picture;
        this.flag = flag;
        this.star = star;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setStar(String star) {
        this.star = Integer.parseInt(star);
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", pubdate='" + pubdate + '\'' +
                ", typeName='" + typeName + '\'' +
                ", flag=" + flag +
                ", star=" + star +
                '}';
    }
}
