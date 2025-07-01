package com.example.ratelimiter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimitController {

    private final RateLimiterService rateLimiterService;

    public RateLimitController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkRateLimit(@RequestHeader("api-key") String apiKey) {
        if (rateLimiterService.isRequestAllowed(apiKey)) {
            return ResponseEntity.ok("Request allowed");
        } else {
            return ResponseEntity.status(429).body("Too Many Requests");
        }
    }
}
