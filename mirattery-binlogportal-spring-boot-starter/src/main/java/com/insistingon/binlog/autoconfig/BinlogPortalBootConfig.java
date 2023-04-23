package com.insistingon.binlog.autoconfig;


import com.insistingon.binlog.config.RedisConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource")
public class BinlogPortalBootConfig {
    private Map<String, DbConfig> dbConfig;
    private Boolean enable = true;
    private Boolean distributedEnable;
    private RedisConfig distributedRedis;
    private RedisConfig positionRedis;
    private HttpHandlerConfig httpHandler;

    private String host;
    private Integer port;
    private String userName;
    private String password;
    private String dbName;
    private List<String> handlerList;
    private List<String> filterTable;

    public Map<String, DbConfig> getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(Map<String, DbConfig> dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getDistributedEnable() {
        return distributedEnable;
    }

    public void setDistributedEnable(Boolean distributedEnable) {
        this.distributedEnable = distributedEnable;
    }

    public RedisConfig getDistributedRedis() {
        return distributedRedis;
    }

    public void setDistributedRedis(RedisConfig distributedRedis) {
        this.distributedRedis = distributedRedis;
    }

    public RedisConfig getPositionRedis() {
        return positionRedis;
    }

    public void setPositionRedis(RedisConfig positionRedis) {
        this.positionRedis = positionRedis;
    }

    public HttpHandlerConfig getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(HttpHandlerConfig httpHandler) {
        this.httpHandler = httpHandler;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<String> handlerList) {
        this.handlerList = handlerList;
    }

    public List<String> getFilterTable() {
        return filterTable;
    }

    public void setFilterTable(List<String> filterTable) {
        this.filterTable = filterTable;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
