package com.script.service;

import com.script.mysql.Database;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final Database database;

    public DatabaseService() throws Exception {
        this.database = new Database("shop");
    }

    public Object exec(String query) throws Exception {
        synchronized (this) {
            return database.exec(query);
        }
    }

    public Object query(String sql) throws Exception {
        return exec(sql);
    }
}
