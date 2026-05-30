package com.script.controller;

import com.script.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Resource
    private RedisService redisService;

    @PostMapping("/exec")
    public Map<String, Object> exec(@RequestBody Map<String, String> body) {
        return Map.of("result", redisService.exec(body.get("command")));
    }

}
