package com.insistingon.binlog.factory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.config.SyncConfig;
import com.insistingon.binlog.event.lifecycle.ILifeCycleFactory;
import com.insistingon.binlog.position.IPositionHandler;

public interface IClientFactory {
    BinaryLogClient getClient(SyncConfig syncConfig) throws BinlogPortalException;

    BinaryLogClient getCachedClient(SyncConfig syncConfig);

    void setPositionHandler(IPositionHandler positionHandler);

    IPositionHandler getPositionHandler();

    void setLifeCycleFactory(ILifeCycleFactory lifeCycleFactory);

    ILifeCycleFactory getLifeCycleFactory();
}
