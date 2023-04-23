package com.insistingon.binlog.event.handler;

import com.insistingon.binlog.event.EventEntity;
import com.insistingon.binlog.BinlogPortalException;

public interface IEventHandler {
    public void process(EventEntity eventEntity) throws BinlogPortalException;
}
