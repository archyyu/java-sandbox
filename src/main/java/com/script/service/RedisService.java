package com.script.service;

import com.script.redis.Redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RedisService {

    private final Redis redis;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RedisService(@Qualifier("mainRedis") Redis redis) {
        this.redis = redis;
    }

    public Object exec(String command) {
        
        logger.info("command:" + command);
        Object object = redis.exec(command);
        return object;
    }
}
