package com.qf.domain;

import java.util.Date;

public class MarketingActivity {
    private Long id;
    private String activityName;
    private String activityType;
    private String activityContent;
    private Date startTime;
    private Date endTime;
    private Integer targetUsers;
    private Integer actualUsers;
    private Integer impressions;
    private Integer clicks;
    private Integer conversions;
    private Double revenue;
    private String status;
    private String createUser;
    private Date createTime;
    private Date updateTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getActivityName() {
        return activityName;
    }
    
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    
    public String getActivityType() {
        return activityType;
    }
    
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    
    public String getActivityContent() {
        return activityContent;
    }
    
    public void setActivityContent(String activityContent) {
        this.activityContent = activityContent;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public Integer getTargetUsers() {
        return targetUsers;
    }
    
    public void setTargetUsers(Integer targetUsers) {
        this.targetUsers = targetUsers;
    }
    
    public Integer getActualUsers() {
        return actualUsers;
    }
    
    public void setActualUsers(Integer actualUsers) {
        this.actualUsers = actualUsers;
    }
    
    public Integer getImpressions() {
        return impressions;
    }
    
    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }
    
    public Integer getClicks() {
        return clicks;
    }
    
    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }
    
    public Integer getConversions() {
        return conversions;
    }
    
    public void setConversions(Integer conversions) {
        this.conversions = conversions;
    }
    
    public Double getRevenue() {
        return revenue;
    }
    
    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreateUser() {
        return createUser;
    }
    
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
