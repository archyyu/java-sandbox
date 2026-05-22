package com.script.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimit {

    private String token;

    private int limit;

    public RateLimit(String token, int limit) {
        this.token = token;
        this.limit = limit;
    }
    
    private Map<Long, Integer> secondCountMap = new ConcurrentHashMap<>();

    public boolean makeRequest(String token, String url) {

        if (!token.equals(this.token)) {
            return false;
        }

        long second = System.currentTimeMillis() / 1000;
        int count = secondCountMap.computeIfAbsent(second, f -> 0);
        if (count < limit) {
            secondCountMap.put(second, count + 1);
            return true;
        }

        return false;
    }

    public int getTheRemainingInCurrentSeconds(String token) {
        if (!token.equals(this.token)) {
            return 0;
        }
        long second = System.currentTimeMillis() / 1000;
        int count = secondCountMap.computeIfAbsent(second,  f -> 0);
        return this.limit - count;
    }

    public void evictOldData() {
        long now = System.currentTimeMillis() / 1000;
        secondCountMap.entrySet().removeIf( e -> {
            if (e.getKey() < now - 3) {
                return true;
            }
            return false;
        });
    }

}
