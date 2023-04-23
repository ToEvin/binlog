package com.insistingon.binlog.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.event.EventEntity;

import java.util.List;

/**
 * 事件解析调度器接口
 */
public interface IEventParserDispatcher {
    public List<EventEntity> parse(Event event) throws BinlogPortalException;
}
