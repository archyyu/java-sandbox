package com.script.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.script.redis.Redis;

@Configuration
public class RedisConfig {
    
    @Bean
    @Qualifier("mainRedis")
    public Redis mainRedis() throws Exception {
        return new Redis("main");
    }

}
