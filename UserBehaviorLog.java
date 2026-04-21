package com.qf.domain;

import java.util.Date;

public class UserBehaviorLog {
    private Long id;
    private Integer userId;
    private String behaviorType;
    private Integer goodsId;
    private String goodsName;
    private String behaviorDetail;
    private Date behaviorTime;
    private String ipAddress;
    private String userAgent;
    private String sessionId;
    private Date createTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getBehaviorType() {
        return behaviorType;
    }
    
    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }
    
    public Integer getGoodsId() {
        return goodsId;
    }
    
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
    
    public String getGoodsName() {
        return goodsName;
    }
    
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    
    public String getBehaviorDetail() {
        return behaviorDetail;
    }
    
    public void setBehaviorDetail(String behaviorDetail) {
        this.behaviorDetail = behaviorDetail;
    }
    
    public Date getBehaviorTime() {
        return behaviorTime;
    }
    
    public void setBehaviorTime(Date behaviorTime) {
        this.behaviorTime = behaviorTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
