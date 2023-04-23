package com.insistingon.binlog.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.insistingon.binlog.event.EventEntity;
import com.insistingon.binlog.event.parser.converter.CommonConverterProcessor;
import com.insistingon.binlog.tablemeta.TableMetaEntity;
import com.insistingon.binlog.tablemeta.TableMetaFactory;
import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.event.EventEntityType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InsertEventParser implements IEventParser {
    CommonConverterProcessor commonConverterProcessor = new CommonConverterProcessor();

    TableMetaFactory tableMetaFactory;

    public InsertEventParser(TableMetaFactory tableMetaFactory) {
        this.tableMetaFactory = tableMetaFactory;
    }

    @Override
    public List<EventEntity> parse(Event event) throws BinlogPortalException {
        WriteRowsEventData writeRowsEventData = event.getData();
        TableMetaEntity tableMetaEntity = tableMetaFactory.getTableMetaEntity(writeRowsEventData.getTableId());
        if (tableMetaEntity == null || writeRowsEventData == null) {
            return null;
        }
        List<Serializable[]> rows = writeRowsEventData.getRows();
        if (rows == null) {
            return null;
        }
        List<EventEntity> eventEntityList = new ArrayList<>();
        rows.forEach(rowMap -> {
            List<TableMetaEntity.ColumnMetaData> columnMetaDataList = tableMetaEntity.getColumnMetaDataList();
            String[] after = commonConverterProcessor.convertToString(rowMap, columnMetaDataList);
            List<String> columns = new ArrayList<>();
            List<Object> changeAfter = new ArrayList<>();
            for (int i = 0; i < after.length; i++) {
                columns.add(columnMetaDataList.get(i).getName());
                changeAfter.add(after[i]);
            }

            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent(event);
            eventEntity.setEventEntityType(EventEntityType.INSERT);
            eventEntity.setDatabaseName(tableMetaEntity.getDbName());
            eventEntity.setTableName(tableMetaEntity.getTableName());
            eventEntity.setColumns(columnMetaDataList);
            eventEntity.setChangeAfter(changeAfter);

            eventEntityList.add(eventEntity);
        });
        return eventEntityList;
    }
}
