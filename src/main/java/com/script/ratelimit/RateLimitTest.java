package com.script.ratelimit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

//SOURCES *

public class RateLimitTest {
    
    public static void main(String[] args) {

        String token = "1213874ashcvaghs12";

        RateLimitService rateLimitService = new RateLimitService();
        String url = "www.google.com";

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i=0;i<3;i++) {
            executor.submit(() -> {

                IntStream.range(0,  200).forEach( k -> {
                    rateLimitService.makeRequestByToken(token, url);
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        
                    }
                });

            });
        }

        executor.shutdown();

        boolean result = true;
        while(result) {
            result = rateLimitService.makeRequestByToken(token, url);
            try {
                Thread.sleep(20);
            } catch (Exception e) {

            }

        }

    }

}
