package com.example.aram.client.feign;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Bybit Feign client
 */
@Configuration
@Slf4j
public class BybitFeignConfig {
    
    /**
     * Configure request options for Bybit API
     */
    @Bean("bybitRequestOptions")
    public Request.Options requestOptions() {
        return new Request.Options(
            10, TimeUnit.SECONDS,  // Connect timeout
            10, TimeUnit.SECONDS,  // Read timeout
            true                   // Follow redirects
        );
    }
    
    /**
     * Configure logging level for Feign client
     */
    @Bean("bybitFeignLoggerLevel")
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    /**
     * Custom error decoder for handling Bybit API errors
     */
    @Bean("bybitErrorDecoder")
    public ErrorDecoder errorDecoder() {
        return new BybitErrorDecoder();
    }
    
    /**
     * Custom error decoder implementation for Bybit API
     */
    public static class BybitErrorDecoder implements ErrorDecoder {
        
        private final ErrorDecoder defaultErrorDecoder = new Default();
        
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            log.error("Bybit API error - Method: {}, Status: {}, Reason: {}", 
                     methodKey, response.status(), response.reason());
            
            // Handle specific Bybit API error codes
            switch (response.status()) {
                case 400:
                    return new RuntimeException("Bad Request - Invalid parameters");
                case 401:
                    return new RuntimeException("Unauthorized - Invalid API key");
                case 403:
                    return new RuntimeException("Forbidden - API key doesn't have required permissions");
                case 429:
                    return new RuntimeException("Rate limit exceeded");
                case 500:
                    return new RuntimeException("Internal server error");
                case 502:
                    return new RuntimeException("Bad gateway");
                case 503:
                    return new RuntimeException("Service unavailable");
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        }
    }
}
