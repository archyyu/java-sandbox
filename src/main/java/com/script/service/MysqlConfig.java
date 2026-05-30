package com.script.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.script.mysql.Database;

@Configuration
public class MysqlConfig {
    
    @Bean
    @Qualifier("shopDatabase")
    public Database shopDatabase() throws Exception{
        return new Database("shop");
    }

}
