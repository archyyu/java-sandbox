package com.script.controller;

import com.script.service.DatabaseService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mysql")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostMapping("/exec")
    public Object exec(@RequestBody Map<String, String> body) throws Exception {
        return databaseService.exec(body.get("query"));
    }

    @PostMapping("/query")
    public Object query(@RequestBody Map<String, String> body) throws Exception {
        return databaseService.query(body.get("sql"));
    }
}
