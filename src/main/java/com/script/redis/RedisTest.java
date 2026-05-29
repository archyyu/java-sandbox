package com.script.redis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//SOURCES Redis.java

public class RedisTest {
    
    public static void main(String[] args) {

        // testRedisBasics();
        loadLogsToRedis();

    }

    public static void loadLogsToRedis() {
        String filePath = "redis-1";
        Redis redis = new Redis();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read each line until the end of the file
            while ((line = br.readLine()) != null) {
                redis.exec(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        System.err.println(redis.exec("GET key"));
        System.err.println(redis.exec("LRANGE list 0 3"));
        
    }

    public static void testRedisBasics() {
        try {
            
            Redis redis = new Redis("redis-1");

            redis.exec("SET key value1");
            System.err.println(redis.exec("GET key"));

            redis.exec("LPUSH list 1 2 3");
            System.err.println(redis.exec("LRANGE list 0 3"));


        } catch (Exception ex) {

        }
    }

    

}
