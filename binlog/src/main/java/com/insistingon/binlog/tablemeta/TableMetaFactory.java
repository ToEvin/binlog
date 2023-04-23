package com.insistingon.binlog.tablemeta;

import com.insistingon.binlog.config.SyncConfig;
import com.insistingon.binlog.BinlogPortalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TableMetaFactory {
    SyncConfig syncConfig;

    private final Logger log = LoggerFactory.getLogger(TableMetaFactory.class);

    public TableMetaFactory(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }

    //缓存tableId信息
    private Map<Long, TableMetaEntity> tableMetaEntityIdMap = new HashMap<>();
    //缓存表名信息
    private Map<String, TableMetaEntity> tableMetaEntityNameMap = new HashMap<>();

    public TableMetaEntity getTableMetaEntity(Long tableId, String dbName, String tableName) throws BinlogPortalException {
        Connection connection = null;
        try {
            if (tableMetaEntityIdMap.get(tableId) != null) {
                return tableMetaEntityIdMap.get(tableId);
            } else {
                //log.info("库名{}", dbName);
                if(!syncConfig.getDbName().equals(dbName)){
                    return null;
                }
                String url = "jdbc:mysql://" + syncConfig.getHost() + ":" + syncConfig.getPort() + "/" + dbName +"?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Hongkong&useAffectedRows=true&useSSL=false";
                connection = DriverManager.getConnection(url, syncConfig.getUserName(), syncConfig.getPassword());
                DatabaseMetaData dbmd = connection.getMetaData();
                ResultSet rs = dbmd.getColumns(dbName, dbName, tableName, null);
                TableMetaEntity tableMetaEntity = new TableMetaEntity();
                tableMetaEntity.setTableId(tableId);
                tableMetaEntity.setDbName(dbName);
                tableMetaEntity.setTableName(tableName);
                while (rs.next()) {
                    TableMetaEntity.ColumnMetaData columnMetaData = new TableMetaEntity.ColumnMetaData();
                    String colName = rs.getString("COLUMN_NAME");
                    columnMetaData.setName(colName);
                    String dbType = rs.getString("TYPE_NAME");
                    columnMetaData.setType(dbType);
                    tableMetaEntity.getColumnMetaDataList().add(columnMetaData);
                }
                tableMetaEntityIdMap.put(tableId, tableMetaEntity);
                return tableMetaEntity;
            }
        } catch (Throwable e) {
            throw new BinlogPortalException(e.getCause());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * 根据tableId获取表元数据
     *
     * @param tableId
     * @return
     */
    public TableMetaEntity getTableMetaEntity(Long tableId) {
        return tableMetaEntityIdMap.get(tableId);
    }
}
