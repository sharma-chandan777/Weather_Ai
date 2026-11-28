
package com.weathersense.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class ApiRequestStats {

    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong successfulRequests = new AtomicLong();
    private final AtomicLong failedRequests = new AtomicLong();
    private final AtomicLong rateLimitedRequests = new AtomicLong();

    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public void recordSuccess() {
        successfulRequests.incrementAndGet();
    }

    public void recordFailure() {
        failedRequests.incrementAndGet();
    }

    public void recordRateLimit() {
        rateLimitedRequests.incrementAndGet();
    }

    public void printStats() {

        System.out.println("========== OPEN-METEO STATS ==========");
        System.out.println("Total Requests: " + totalRequests.get());
        System.out.println("Successful: " + successfulRequests.get());
        System.out.println("Failed: " + failedRequests.get());
        System.out.println("429 Errors: " + rateLimitedRequests.get());
        System.out.println("======================================");
    }
}
