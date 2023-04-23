package com.insistingon.binlog.distributed;

import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.config.BinlogPortalConfig;
import com.insistingon.binlog.config.RedisConfig;
import com.insistingon.binlog.config.SyncConfig;
import com.insistingon.binlog.factory.IClientFactory;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RedisDistributedHandler implements IDistributedHandler {

    private final Logger log = LoggerFactory.getLogger(RedisDistributedHandler.class);

    //redis配置，支持集群模式
    RedisConfig redisConfig;
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0,5,
                                      60L,TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());

    private static String REDIS_KEY = "binlog:portal:";

    public RedisDistributedHandler(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    public void start(BinlogPortalConfig binlogPortalConfig) throws BinlogPortalException {
        if (redisConfig == null) {
            throw new BinlogPortalException("redis config can not be null");
        }
        Config config = new Config();

        String[] checkRedisNodes = redisConfig.getHost().split(",");
        if (checkRedisNodes.length > 1) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            for (String node : checkRedisNodes) {
                String[] split = node.split(":");
                clusterServersConfig.addNodeAddress("redis://" + split[0] + ":" + NumberUtils.toInt(split[1]));
            }
            if (!StringUtils.isBlank(redisConfig.getPassword())) {
                clusterServersConfig.setPassword(redisConfig.getPassword());
            }
        } else {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress("redis://" + redisConfig.getHost());
            if (!StringUtils.isBlank(redisConfig.getPassword())) {
                singleServerConfig.setPassword(redisConfig.getPassword());
            }
        }

        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);

        //新建工厂对象
        IClientFactory binaryLogClientFactory = binlogPortalConfig.getClientFactory();
        binaryLogClientFactory.setPositionHandler(binlogPortalConfig.getPositionHandler());
        binaryLogClientFactory.setLifeCycleFactory(binlogPortalConfig.getLifeCycleFactory());

        //定时创建客户端,抢到锁的就创建
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                binlogPortalConfig.getSyncConfigMap().forEach((key, syncConfig) -> {
                    String lockStr = REDIS_KEY + Md5Crypt.md5Crypt(syncConfig.toString().getBytes(), null, "");
                    RLock lock = redisson.getLock(lockStr);
                    try {
                        if (lock.tryLock(1, 1, TimeUnit.MINUTES)) {
                            binaryLogClientFactory.getClient(syncConfig).connect();
                        }
                    } catch (BinlogPortalException | IOException | InterruptedException e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        lock.unlock();
                    }
                });
            }
        }, 0, 1000);
        /*binlogPortalConfig.getSyncConfigMap().forEach((key, syncConfig) -> {
            threadPoolExecutor.execute(() -> {
                String lockStr = REDIS_KEY + Md5Crypt.md5Crypt(syncConfig.toString().getBytes(), null, "");
                RLock lock = redisson.getLock(lockStr);
                try {
                    if (lock.tryLock(1, 30, TimeUnit.MINUTES)) {
                        binaryLogClientFactory.getClient(syncConfig).connect();
                    }
                } catch (BinlogPortalException | IOException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    lock.unlock();
                }
            });
        });*/
    }

    public Boolean canBuild(SyncConfig syncConfig) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock("myLock");
        lock.lock();

        return null;
    }

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.setLockWatchdogTimeout(10000L);
        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock("myLock");
        lock.lock();
    }
}
