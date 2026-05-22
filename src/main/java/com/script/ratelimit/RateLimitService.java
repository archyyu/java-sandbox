package com.script.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitService {
    
    private Map<String, RateLimit> tokenLimitor = new ConcurrentHashMap<>();

    private final int limit = 500;

    //this is small project for purpose of ratelimitor for token/user
    //it leverages the Map, the timestamp(by second) as the key, the value is the aggregated count of hits by the token.
    //it is simple, but enough for most cases.
    
    public RateLimitService() {

    }

    public boolean makeRequestByToken(String token, String url) {

        RateLimit rateLimit = tokenLimitor.computeIfAbsent(token, f -> new RateLimit(token, this.limit));
        boolean result = rateLimit.makeRequest(token, url);
        System.err.println(Thread.currentThread().getName() + " " + token + " made a request, result: " + result + ", at:" + System.currentTimeMillis()/1000 + " remaining:" + rateLimit.getTheRemainingInCurrentSeconds(token));
        
        return result;

    }

}
