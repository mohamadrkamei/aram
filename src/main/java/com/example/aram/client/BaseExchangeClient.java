package com.example.aram.client;

import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Base class for exchange clients with common functionality
 */
@Slf4j
public abstract class BaseExchangeClient implements ExchangeClient {
    
    protected final WebClient webClient;
    protected final ExchangeType exchangeType;
    protected static final Duration TIMEOUT = Duration.ofSeconds(10);
    
    protected BaseExchangeClient(WebClient.Builder webClientBuilder, String baseUrl, ExchangeType exchangeType) {
        if (webClientBuilder != null && baseUrl != null) {
            this.webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .build();
        } else {
            this.webClient = null;
        }
        this.exchangeType = exchangeType;
    }
    
    @Override
    public ExchangeType getExchangeType() {
        return exchangeType;
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // Try to fetch a common trading pair to check connectivity
            PriceDto price = fetchPrice("BTC/USDT");
            return price != null;
        } catch (Exception e) {
            log.error("Health check failed for {}: {}", exchangeType, e.getMessage());
            return false;
        }
    }
    
    /**
     * Convert exchange-specific symbol format to standard format (e.g., BTC/USDT)
     */
    protected abstract String convertToExchangeSymbol(String symbol);
    
    /**
     * Convert exchange symbol to standard format
     */
    protected abstract String convertFromExchangeSymbol(String exchangeSymbol);
    
    /**
     * Handle API errors with retry logic
     */
    protected <T> Mono<T> handleApiCall(Mono<T> apiCall) {
        if (apiCall == null) {
            return Mono.empty();
        }
        return apiCall
                .timeout(TIMEOUT)
                .retry(2)
                .doOnError(error -> log.error("API call failed for {}: {}", exchangeType, error.getMessage()))
                .onErrorReturn(null);
    }
    
    /**
     * Create a price DTO with timestamp
     */
    protected PriceDto createPriceDto(String symbol) {
        return PriceDto.builder()
                .exchange(exchangeType)
                .symbol(symbol)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

