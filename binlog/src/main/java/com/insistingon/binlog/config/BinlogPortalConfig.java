package com.insistingon.binlog.config;

import com.insistingon.binlog.distributed.IDistributedHandler;
import com.insistingon.binlog.event.lifecycle.BaseLifeCycleListenerFactory;
import com.insistingon.binlog.event.lifecycle.ILifeCycleFactory;
import com.insistingon.binlog.factory.BinaryLogClientFactory;
import com.insistingon.binlog.factory.IClientFactory;
import com.insistingon.binlog.position.IPositionHandler;

import java.util.HashMap;
import java.util.Map;

public class BinlogPortalConfig {
    //配置列表
    Map<String, SyncConfig> syncConfigList = new HashMap<>();

    //binlog位点处理器
    IPositionHandler positionHandler;

    //分布式处理器
    IDistributedHandler distributedHandler;

    //LifeCycleEvent监听器
    ILifeCycleFactory lifeCycleFactory = new BaseLifeCycleListenerFactory();

    IClientFactory clientFactory = new BinaryLogClientFactory();

    //增加配置项
    public void addSyncConfig(String key, SyncConfig syncConfig) {
        syncConfigList.put(key, syncConfig);
    }

    public Map<String, SyncConfig> getSyncConfigMap() {
        return syncConfigList;
    }

    public IPositionHandler getPositionHandler() {
        return positionHandler;
    }

    public void setPositionHandler(IPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public IDistributedHandler getDistributedHandler() {
        return distributedHandler;
    }

    public void setDistributedHandler(IDistributedHandler distributedHandler) {
        this.distributedHandler = distributedHandler;
    }

    public ILifeCycleFactory getLifeCycleFactory() {
        return lifeCycleFactory;
    }

    public void setLifeCycleFactory(ILifeCycleFactory lifeCycleFactory) {
        this.lifeCycleFactory = lifeCycleFactory;
    }

    public IClientFactory getClientFactory() {
        return clientFactory;
    }

    public void setClientFactory(IClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
