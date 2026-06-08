package com.script.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;

import com.script.redis.Redis;

@Configuration
public class RedisConfig {
    
    @Bean
    @Qualifier("mainRedis")
    public Redis mainRedis() throws Exception {
        return new Redis("main");
    }

    @EventListener(ApplicationReadyEvent.class) 
    public void loadRedis() throws Exception {
        Redis redis = this.mainRedis();
        redis.readFromSnapShot();
        redis.readFromLogs();
    }

}
