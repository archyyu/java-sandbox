package com.script.service;

import com.script.redis.Redis;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RedisService {

    private final Redis redis;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RedisService() {
        this.redis = new Redis();
    }

    public Object exec(String command) {
        
        logger.info("command:" + command);
        Object object = redis.exec(command);
        return object;
    }

    public String get(String key) {
        return (String) exec("GET " + key);
    }

    public void set(String key, String value) {
        exec("SET " + key + " " + value);
    }

    public void del(String key) {
        exec("DEL " + key);
    }

    public void lpush(String key, String... values) {
        exec("LPUSH " + key + " " + String.join(" ", values));
    }

    public void rpush(String key, String... values) {
        exec("RPUSH " + key + " " + String.join(" ", values));
    }

    public Object lrange(String key, int left, int right) {
        return exec("LRANGE " + key + " " + left + " " + right);
    }

    public void sadd(String key, String... members) {
        exec("SADD " + key + " " + String.join(" ", members));
    }

    public void srem(String key, String... members) {
        exec("SREM " + key + " " + String.join(" ", members));
    }

    public Object smembers(String key) {
        return exec("SMEMBERS " + key);
    }

    public void hset(String key, String field, String value) {
        exec("HSET " + key + " " + field + " " + value);
    }

    public String hget(String key, String field) {
        return (String) exec("HGET " + key + " " + field);
    }
}
