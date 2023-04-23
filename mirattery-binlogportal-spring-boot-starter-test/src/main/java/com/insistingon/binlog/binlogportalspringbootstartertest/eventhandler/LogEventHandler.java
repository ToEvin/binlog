package com.insistingon.binlog.binlogportalspringbootstartertest.eventhandler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;

import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.binlogportalspringbootstartertest.entity.BizBrand;
import com.insistingon.binlog.event.EventEntity;
import com.insistingon.binlog.event.EventEntityType;
import com.insistingon.binlog.event.handler.IEventHandler;
import com.insistingon.binlog.tablemeta.TableMetaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LogEventHandler implements IEventHandler {
    public void process(EventEntity eventEntity) throws BinlogPortalException {
        log.info(eventEntity.getJsonFormatData());
        if (!"mirattery_test".equals(eventEntity.getDatabaseName())) {
            return;
        }

        List<TableMetaEntity.ColumnMetaData> columns = eventEntity.getColumns();
        if ("biz_brand".equals(eventEntity.getTableName())) {
            if (EventEntityType.UPDATE.getDesc().equals(eventEntity.getEventEntityType().getDesc())) {
                JSONObject changeBefore = new JSONObject();
                JSONObject changeAfter = new JSONObject();
                for (int i = 0; i < columns.size(); i++) {
                    String fileName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, columns.get(i).getName());
                    changeBefore.put(fileName, eventEntity.getChangeBefore().get(i));
                    changeAfter.put(fileName, eventEntity.getChangeAfter().get(i));
                }
                BizBrand beforeBrand = JSONObject.toJavaObject(changeBefore, BizBrand.class);
                BizBrand afterBrand = JSONObject.toJavaObject(changeAfter, BizBrand.class);
                log.info("before:{},after:{}", JSONObject.toJSONString(beforeBrand), JSONObject.toJSONString(afterBrand));
            }
        }
    }
}
