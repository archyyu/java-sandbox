package com.script.service;

import com.script.mysql.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final Database database;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DatabaseService(@Qualifier("shopDatabase") Database database) throws Exception {
        this.database = database;
    }

    public Object exec(String query) throws Exception {
        logger.info("query:" + query);
        return database.exec(query);
    }

}
