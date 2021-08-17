package com.netflix.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Main Class to start the server
 */
@SpringBootApplication
@EnableZuulProxy
public class ServerStartUp {

    public static void main(String[] args) {
        SpringApplication.run(ServerStartUp.class, args);
    }
}
