package com.qf.data.vo;

public class DataSourceConfig {
    private String jdbcUrl;
    private String username;
    private String password;
    private String dataWarehouseRoot;
    private int batchSize = 1000;
    private int threadPoolSize = 4;

    // Getters and Setters
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
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

    public String getDataWarehouseRoot() {
        return dataWarehouseRoot;
    }

    public void setDataWarehouseRoot(String dataWarehouseRoot) {
        this.dataWarehouseRoot = dataWarehouseRoot;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    @Override
    public String toString() {
        return "DataSourceConfig{" +
                "jdbcUrl='" + jdbcUrl + '\'' +
                ", dataWarehouseRoot='" + dataWarehouseRoot + '\'' +
                ", batchSize=" + batchSize +
                ", threadPoolSize=" + threadPoolSize +
                '}';
    }
}