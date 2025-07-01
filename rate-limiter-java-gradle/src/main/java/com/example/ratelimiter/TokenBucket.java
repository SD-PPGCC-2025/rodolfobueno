package com.example.ratelimiter;

import java.time.Instant;

public class TokenBucket {
    private final int capacity;
    private final int refillRatePerSecond;
    private double tokens;
    private Instant lastRefillTimestamp;

    public TokenBucket(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTimestamp = Instant.now();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        Instant now = Instant.now();
        long secondsSinceLastRefill = lastRefillTimestamp.until(now, java.time.temporal.ChronoUnit.SECONDS);
        if (secondsSinceLastRefill > 0) {
            double tokensToAdd = secondsSinceLastRefill * refillRatePerSecond;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}
