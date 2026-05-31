package com.script.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.script.service.KafkaService;

import jakarta.annotation.Resource;

import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    
    @Resource
    private KafkaService kafkaService;


    @PostMapping("/{topic}/{consumer}")
    public ResponseEntity<Object> consume(@PathVariable String topic, @PathVariable String consumer) {

        Object object = this.kafkaService.consumeMessage(consumer, topic);
        if (object != null) {
            return ResponseEntity.ok(object);
        } else {
            return ResponseEntity.badRequest().body(Map.of("message","no"));
        }

    }

    @PostMapping("/{topic}/produce")
    public ResponseEntity<Object> product(@PathVariable String topic, @RequestParam String body) {
        
        this.kafkaService.produceMessage((Object)body, topic);
        return ResponseEntity.ok("");

    }

}
