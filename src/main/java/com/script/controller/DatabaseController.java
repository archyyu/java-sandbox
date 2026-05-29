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
    public Map<String, Object> exec(@RequestBody Map<String, String> body) throws Exception {
        return Map.of("result", databaseService.exec(body.get("query")));
    }

}
