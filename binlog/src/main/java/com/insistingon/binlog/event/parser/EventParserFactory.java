package com.insistingon.binlog.event.parser;

import com.insistingon.binlog.config.SyncConfig;
import com.insistingon.binlog.tablemeta.TableMetaFactory;
import com.insistingon.binlog.BinlogPortalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件解析器工厂
 */
public class EventParserFactory {
    private static final Logger log = LoggerFactory.getLogger(EventParserFactory.class);

    /**
     * 获取事件解析调度器
     *
     * @param syncConfig
     * @return
     */
    public static IEventParserDispatcher getEventParserDispatcher(SyncConfig syncConfig) throws BinlogPortalException {
        //目前只有一种解析器，这里可扩展为根据syncConfig的配置获取不同的解析器
        return new CommonEventParserDispatcher(new TableMetaFactory(syncConfig));
    }
}
