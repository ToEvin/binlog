package com.insistingon.binlog.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
public class BinlogRedisConfig {
    String host;
    Integer port;
    String password;
    RedisClusterConfig cluster;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RedisClusterConfig getCluster() {
        return cluster;
    }

    public void setCluster(RedisClusterConfig cluster) {
        this.cluster = cluster;
    }
}
