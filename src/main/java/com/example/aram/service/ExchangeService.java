package com.example.aram.service;

import com.example.aram.client.ExchangeClient;
import com.example.aram.client.ExchangeClientFactory;
import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.example.aram.enums.OrderSide;
import com.example.aram.enums.OrderType;
import com.example.aram.model.Exchange;
import com.example.aram.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to interact with cryptocurrency exchanges
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {
    
    private final ExchangeRepository exchangeRepository;
    private final ExchangeClientFactory clientFactory;
    
    public List<Exchange> getAllActiveExchanges() {
        return exchangeRepository.findByEnabledTrueAndIsHealthyTrue();
    }
    
    public Exchange getExchange(ExchangeType exchangeType) {
        return exchangeRepository.findByExchangeType(exchangeType)
                .orElseThrow(() -> new RuntimeException("Exchange not found: " + exchangeType));
    }
    
    /**
     * Fetch current price for a trading pair from an exchange
     */
    public PriceDto fetchPrice(ExchangeType exchangeType, String symbol) {
        log.info("Fetching price for {} from {}", symbol, exchangeType);
        
        return clientFactory.getClient(exchangeType)
                .map(client -> client.fetchPrice(symbol))
                .orElse(null);
    }
    
    /**
     * Fetch prices from all available exchanges
     */
    public List<PriceDto> fetchPriceFromAllExchanges(String symbol) {
        log.info("Fetching price for {} from all exchanges", symbol);
        
        List<PriceDto> prices = new ArrayList<>();
        for (ExchangeClient client : clientFactory.getAllClients()) {
            try {
                PriceDto price = client.fetchPrice(symbol);
                if (price != null) {
                    prices.add(price);
                }
            } catch (Exception e) {
                log.error("Error fetching price from {}: {}", client.getExchangeType(), e.getMessage());
            }
        }
        return prices;
    }
    
    /**
     * Fetch all tickers from a specific exchange
     */
    public List<PriceDto> fetchAllTickers(ExchangeType exchangeType) {
        return clientFactory.getClient(exchangeType)
                .map(ExchangeClient::fetchAllTickers)
                .orElse(new ArrayList<>());
    }
    
    /**
     * Execute a trade on an exchange
     */
    public String executeTrade(ExchangeType exchangeType, String symbol, 
                              OrderType orderType, OrderSide orderSide,
                              BigDecimal price, BigDecimal quantity) {
        // TODO: Implement actual trade execution
        log.info("Executing {} {} order for {} on {} at price {} quantity {}", 
                orderSide, orderType, symbol, exchangeType, price, quantity);
        
        return "ORDER_" + System.currentTimeMillis();
    }
    
    /**
     * Check if exchange API is responding
     */
    public boolean checkHealth(ExchangeType exchangeType) {
        return clientFactory.getClient(exchangeType)
                .map(ExchangeClient::isHealthy)
                .orElse(false);
    }
    
    /**
     * Get account balance for a currency
     */
    public BigDecimal getBalance(ExchangeType exchangeType, String currency) {
        // TODO: Implement actual balance checking
        log.info("Getting balance for {} on {}", currency, exchangeType);
        return BigDecimal.ZERO;
    }
    
    /**
     * Get all available exchange clients
     */
    public List<ExchangeClient> getAllClients() {
        return clientFactory.getAllClients();
    }
}

