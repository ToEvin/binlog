package com.insistingon.binlog.distributed;

import com.insistingon.binlog.config.BinlogPortalConfig;
import com.insistingon.binlog.BinlogPortalException;

public interface IDistributedHandler {
    void start(BinlogPortalConfig binlogPortalConfig) throws BinlogPortalException;
}
