package com.script.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.script.mysql.Database;

@Configuration
public class MysqlConfig {
    
    @Bean
    @Qualifier("shopDatabase")
    public Database shopDatabase() throws Exception{
        return new Database("shop");
    }

    @EventListener(ApplicationReadyEvent.class) 
    public void loadMysqlData() throws Exception{

        Database database = this.shopDatabase();
        database.readFromSnapShot();
        database.readFromLogs();

    }

}
