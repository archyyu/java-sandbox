package com.script.service;

import com.script.mysql.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final Database database;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DatabaseService() throws Exception {
        this.database = new Database("shop");
    }

    public Object exec(String query) throws Exception {
        logger.info("query:" + query);
        return database.exec(query);
    }

}
