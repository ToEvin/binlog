package com.insistingon.binlog.event.lifecycle;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlog.config.SyncConfig;

public class BaseLifeCycleListenerFactory implements ILifeCycleFactory {
    @Override
    public BinaryLogClient.LifecycleListener getLifeCycleListener(SyncConfig syncConfig) {
        return new BaseLifeCycleEventListener(syncConfig);
    }
}
