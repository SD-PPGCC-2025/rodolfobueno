package com.example.ratelimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

public class RateLimiterStressTest {

    @Test
    void stressTestWithHashMap() {
        RateLimiterService service = new RateLimiterService(); // Make sure this uses HashMap
        ExecutorService executor = Executors.newFixedThreadPool(20);

        String[] apiKeys = {"a", "b", "c"};

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.submit(() -> {
                String apiKey = apiKeys[finalI % apiKeys.length];
                boolean allowed = service.isRequestAllowed(apiKey);
                System.out.println(Thread.currentThread().getName() + " - " + apiKey + ": " + (allowed ? "✅ Allowed" : "❌ Rejected"));
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // wait
        }
    }
}