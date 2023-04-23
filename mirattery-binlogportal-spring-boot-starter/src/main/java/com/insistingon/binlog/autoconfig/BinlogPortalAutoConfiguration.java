package com.insistingon.binlog.autoconfig;


import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.BinlogPortalStarter;
import com.insistingon.binlog.config.BinlogPortalConfig;
import com.insistingon.binlog.config.RedisConfig;
import com.insistingon.binlog.config.SyncConfig;
import com.insistingon.binlog.distributed.RedisDistributedHandler;
import com.insistingon.binlog.event.handler.HttpRequestEventHandler;
import com.insistingon.binlog.event.handler.IEventHandler;
import com.insistingon.binlog.event.handler.IHttpCallback;
import com.insistingon.binlog.position.RedisPositionHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Configuration
@EnableConfigurationProperties({BinlogPortalBootConfig.class, BinlogRedisConfig.class})
@ConditionalOnClass(BinlogPortalStarter.class)
//@ConditionalOnProperty(prefix = "binlogportal", value = "enable", havingValue = "true")
public class BinlogPortalAutoConfiguration {
    @Autowired
    BinlogPortalBootConfig binlogPortalBootConfig;

    @Autowired
    BinlogRedisConfig binlogRedisConfig;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(BinlogPortalStarter.class)
    public BinlogPortalStarter binlogPortalStarter() throws BinlogPortalException {
        if(!binlogPortalBootConfig.getEnable()){
            return new BinlogPortalStarter();
        }
        BinlogPortalConfig binlogPortalConfig = new BinlogPortalConfig();
        Map<String, IEventHandler> eventHandlerList = applicationContext.getBeansOfType(IEventHandler.class);

        //common http event handler
        HttpRequestEventHandler httpRequestEventHandler = null;
        HttpHandlerConfig httpHandlerConfig = binlogPortalBootConfig.getHttpHandler();
        if (httpHandlerConfig != null && httpHandlerConfig.getUrlList() != null) {
            httpRequestEventHandler = new HttpRequestEventHandler();
            httpRequestEventHandler.setUrlList(binlogPortalBootConfig.getHttpHandler().getUrlList());
            if (!StringUtils.isBlank(httpHandlerConfig.getResultCallback())) {
                httpRequestEventHandler.setHttpCallback(applicationContext.getBeansOfType(IHttpCallback.class).get(httpHandlerConfig.getResultCallback()));
            }
        }

        //dbconfig list
        if(binlogPortalBootConfig.getDbConfig() != null){
            for (Map.Entry<String, DbConfig> entry : binlogPortalBootConfig.getDbConfig().entrySet()) {
                String key = entry.getKey();
                DbConfig val = entry.getValue();
                SyncConfig syncConfig = new SyncConfig();
                syncConfig.setHost(val.getHost());
                syncConfig.setPort(val.getPort());
                syncConfig.setUserName(val.getUserName());
                syncConfig.setPassword(val.getPassword());
                syncConfig.setDbName(val.getDbName());
                if (val.getHandlerList() != null) {
                    val.getHandlerList().forEach(eventHandler -> {
                        syncConfig.addEventHandlerList(eventHandlerList.get(eventHandler));
                    });
                }
                if(!CollectionUtils.isEmpty(val.getFilterTable())){
                    syncConfig.setFilterTable(val.getFilterTable());
                }
            /*if (httpRequestEventHandler != null) {
                syncConfig.addEventHandlerList(httpRequestEventHandler);
            }*/
                binlogPortalConfig.addSyncConfig(key, syncConfig);
            }
        }
        SyncConfig syncConfig = new SyncConfig();
        syncConfig.setHost(binlogPortalBootConfig.getHost());
        syncConfig.setPort(binlogPortalBootConfig.getPort());
        syncConfig.setUserName(binlogPortalBootConfig.getUserName());
        syncConfig.setPassword(binlogPortalBootConfig.getPassword());
        syncConfig.setDbName(binlogPortalBootConfig.getDbName());
        if (binlogPortalBootConfig.getHandlerList() != null) {
            binlogPortalBootConfig.getHandlerList().forEach(eventHandler -> {
                syncConfig.addEventHandlerList(eventHandlerList.get(eventHandler));
            });
        }
        if (!CollectionUtils.isEmpty(binlogPortalBootConfig.getFilterTable())) {
            syncConfig.setFilterTable(binlogPortalBootConfig.getFilterTable());
        }
        binlogPortalConfig.addSyncConfig("key", syncConfig);

        //redis config
        RedisConfig positionRedisConfig = new RedisConfig();
        if (binlogRedisConfig.getHost() != null) {
            positionRedisConfig.setHost(binlogRedisConfig.getHost() + ":" + binlogRedisConfig.getPort());
        }
        if (binlogRedisConfig.getCluster() != null && binlogRedisConfig.getCluster().getNodes() != null) {
            positionRedisConfig.setHost(binlogRedisConfig.getCluster().getNodes());
        }
        positionRedisConfig.setPassword(binlogRedisConfig.getPassword());
        //binlog position config
        //RedisConfig positionRedisConfig = binlogPortalBootConfig.getPositionRedis();
        if (positionRedisConfig != null) {
            RedisPositionHandler redisPositionHandler = new RedisPositionHandler(positionRedisConfig);
            binlogPortalConfig.setPositionHandler(redisPositionHandler);
        } else {
            throw new BinlogPortalException("binlog position redis should not be null");
        }

        //distributed config
        /*if (binlogPortalBootConfig.getDistributedEnable()) {
            binlogPortalConfig.setDistributedHandler(new RedisDistributedHandler(binlogPortalBootConfig.getDistributedRedis()));
        }*/
        binlogPortalConfig.setDistributedHandler(new RedisDistributedHandler(positionRedisConfig));

        BinlogPortalStarter binlogPortalStarter = new BinlogPortalStarter();
        binlogPortalStarter.setBinlogPortalConfig(binlogPortalConfig);
        return binlogPortalStarter;
    }
}
