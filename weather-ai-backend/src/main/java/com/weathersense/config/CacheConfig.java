
package com.weathersense.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager manager =
                new CaffeineCacheManager();

        manager.registerCustomCache(
                "weatherCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(15))
                        .maximumSize(1000)
                        .build()
        );

        manager.registerCustomCache(
                "geocodingCache",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofHours(24))
                        .maximumSize(500)
                        .build()
        );

        return manager;
    }
}
