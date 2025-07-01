package com.example.ratelimiter;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, TokenBucket> buckets = new HashMap<>();
    // private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public synchronized boolean isRequestAllowed(String apiKey) {
        try {
           // System.out.println(Thread.currentThread().getName() + " - Checking API key: " + apiKey);
            
           // CRITICAL SECTION
            if (!buckets.containsKey(apiKey)) {
                System.out.println(Thread.currentThread().getName() + " - Creating new bucket for: " + apiKey);
                buckets.put(apiKey, new TokenBucket(10, 1));
            }

            TokenBucket bucket = buckets.get(apiKey);
            return bucket.allowRequest();
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + " - ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
