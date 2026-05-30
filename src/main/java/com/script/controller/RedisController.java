package com.script.controller;

import com.script.service.RedisService;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/get/{key}")
    public String get(@PathVariable String key) {
        return redisService.get(key);
    }

    @PostMapping("/set")
    public void set(@RequestBody Map<String, String> body) {
        redisService.set(body.get("key"), body.get("value"));
    }

    @DeleteMapping("/del/{key}")
    public void del(@PathVariable String key) {
        redisService.del(key);
    }

    @PostMapping("/lpush")
    public void lpush(@RequestBody Map<String, Object> body) {
        String key = (String) body.get("key");
        List<String> values = ((List<?>) body.get("values")).stream().map(Object::toString).toList();
        redisService.lpush(key, values.toArray(new String[0]));
    }

    @PostMapping("/rpush")
    public void rpush(@RequestBody Map<String, Object> body) {
        String key = (String) body.get("key");
        List<String> values = ((List<?>) body.get("values")).stream().map(Object::toString).toList();
        redisService.rpush(key, values.toArray(new String[0]));
    }

    @GetMapping("/lrange/{key}")
    public Object lrange(@PathVariable String key,
                         @RequestParam(defaultValue = "0") int left,
                         @RequestParam(defaultValue = "-1") int right) {
        return redisService.lrange(key, left, right);
    }

    @PostMapping("/sadd")
    public void sadd(@RequestBody Map<String, Object> body) {
        String key = (String) body.get("key");
        List<String> members = ((List<?>) body.get("members")).stream().map(Object::toString).toList();
        redisService.sadd(key, members.toArray(new String[0]));
    }

    @PostMapping("/srem")
    public void srem(@RequestBody Map<String, Object> body) {
        String key = (String) body.get("key");
        List<String> members = ((List<?>) body.get("members")).stream().map(Object::toString).toList();
        redisService.srem(key, members.toArray(new String[0]));
    }

    @GetMapping("/smembers/{key}")
    public Object smembers(@PathVariable String key) {
        return redisService.smembers(key);
    }

    @PostMapping("/hset")
    public void hset(@RequestBody Map<String, String> body) {
        redisService.hset(body.get("key"), body.get("field"), body.get("value"));
    }

    @GetMapping("/hget/{key}/{field}")
    public String hget(@PathVariable String key, @PathVariable String field) {
        return redisService.hget(key, field);
    }
}
