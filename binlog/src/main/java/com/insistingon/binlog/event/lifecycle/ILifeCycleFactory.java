package com.insistingon.binlog.event.lifecycle;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlog.config.SyncConfig;

public interface ILifeCycleFactory {
    BinaryLogClient.LifecycleListener getLifeCycleListener(SyncConfig syncConfig);
}
