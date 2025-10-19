package com.example.aram.client;

import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;

import java.util.List;

/**
 * Interface for exchange client implementations
 */
public interface ExchangeClient {
    
    /**
     * Get the exchange type this client handles
     */
    ExchangeType getExchangeType();
    
    /**
     * Fetch price for a single trading pair
     */
    PriceDto fetchPrice(String symbol);
    
    /**
     * Fetch prices for multiple trading pairs
     */
    List<PriceDto> fetchPrices(List<String> symbols);
    
    /**
     * Check if the exchange API is accessible
     */
    boolean isHealthy();
    
    /**
     * Get ticker information for all supported pairs
     */
    List<PriceDto> fetchAllTickers();
}

