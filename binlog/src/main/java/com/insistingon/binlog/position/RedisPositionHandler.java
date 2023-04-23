package com.insistingon.binlog.position;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insistingon.binlog.BinlogPortalException;
import com.insistingon.binlog.config.RedisConfig;
import com.insistingon.binlog.config.SyncConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

public class RedisPositionHandler implements IPositionHandler {

    private final static Logger log = LoggerFactory.getLogger(RedisPositionHandler.class);

    private RedisConfig redisConfig;
    private Jedis jedis;
    private JedisPool jedisPool;
    private JedisCluster jedisCluster;

    public RedisPositionHandler(RedisConfig redisConfig) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);

        String[] checkRedisNodes = redisConfig.getHost().split(",");

        if (checkRedisNodes.length > 1) {
            Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
            for (String node : checkRedisNodes) {
                String[] split = node.split(":");
                jedisClusterNode.add(new HostAndPort(split[0], NumberUtils.toInt(split[1])));
            }
            if (StringUtils.isNotBlank(redisConfig.getPassword())) {
                jedisCluster = new JedisCluster(jedisClusterNode, 6000, 5000, 10, redisConfig.getPassword(), jedisPoolConfig);
            } else {
                jedisCluster = new JedisCluster(jedisClusterNode, 6000, 5000, 10, jedisPoolConfig);
            }
        } else {
            for (String node : checkRedisNodes) {
                String[] split = node.split(":");
                if (!StringUtils.isBlank(redisConfig.getPassword())) {
                    jedisPool = new JedisPool(jedisPoolConfig, split[0], NumberUtils.toInt(split[1]), 1000, redisConfig.getPassword());
                } else {
                    jedisPool = new JedisPool(jedisPoolConfig, split[0], NumberUtils.toInt(split[1]), 1000);
                }
            }
        }
    }

    @Override
    public BinlogPositionEntity getPosition(SyncConfig syncConfig) throws BinlogPortalException {
        //String key = syncConfig.getHost() + ":" + syncConfig.getPort() + ":" + syncConfig.getDbName();
        String key = "binlog:position:" + syncConfig.getDbName();
        if (jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                String position = jedis.get(key);
                if (position != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(position, BinlogPositionEntity.class);
                }
            } catch (JsonProcessingException e) {
                return null;
            } finally {
                if (jedis != null) jedis.close();
            }
        } else if (jedisCluster != null) {
            String position = jedisCluster.get(key);
            try {
                if (position != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(position, BinlogPositionEntity.class);
                }
            } catch (JsonProcessingException e) {
                return null;
            } finally {
                //if (jedisCluster != null) jedisCluster.close();
            }
        }
        return null;
    }

    @Override
    public void savePosition(SyncConfig syncConfig, BinlogPositionEntity binlogPositionEntity) throws BinlogPortalException {
        ObjectMapper objectMapper = new ObjectMapper();
        //int seconds = 1 * 24 * 60 * 60;
        int seconds = 1;
        //String key = syncConfig.getHost() + ":" + syncConfig.getPort() + ":" + syncConfig.getDbName();
        String key = "binlog:position:" + syncConfig.getDbName();
        if (jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                //jedis.set(key, objectMapper.writeValueAsString(binlogPositionEntity));
                jedis.setex(key, seconds, objectMapper.writeValueAsString(binlogPositionEntity));
            } catch (JsonProcessingException e) {
                throw new BinlogPortalException("save position error!" + binlogPositionEntity.toString(), e);
            } finally {
                if (jedis != null) jedis.close();
            }
        } else if (jedisCluster != null) {
            try {
                //String set = jedisCluster.set(key, objectMapper.writeValueAsString(binlogPositionEntity));
                jedisCluster.setex(key,seconds,objectMapper.writeValueAsString(binlogPositionEntity));
            } catch (JsonProcessingException e) {
                throw new BinlogPortalException("save position error!" + binlogPositionEntity.toString(), e);
            } finally {
                //if (jedisCluster != null) jedisCluster.close();
            }
        }
    }
}
