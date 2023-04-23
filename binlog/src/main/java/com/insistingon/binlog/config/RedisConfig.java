package com.insistingon.binlog.config;

/**
 * 内部抽象redis配置
 */
public class RedisConfig {
    String host;
    String password;
    Integer port;
    String nodes;

    public RedisConfig() {
    }

    public RedisConfig(String host) {
        this.host = host;
    }

    public RedisConfig(String host, String password) {
        this.host = host;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "RedisConfig{" +
                "host='" + host + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
