package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.client.feign.CoinbaseFeignClient;
import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Coinbase Pro API Client
 * API Documentation: https://docs.cloud.coinbase.com/exchange/reference
 */
@Component
@Slf4j
public class CoinbaseClient extends BaseExchangeClient {
    
    private final CoinbaseFeignClient coinbaseFeignClient;
    
    public CoinbaseClient(CoinbaseFeignClient coinbaseFeignClient) {
        super(null, null, ExchangeType.COINBASE);
        this.coinbaseFeignClient = coinbaseFeignClient;
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String coinbaseSymbol = convertToExchangeSymbol(symbol);
            
            CoinbaseTickerResponse response = coinbaseFeignClient.getTicker(coinbaseSymbol);
            
            if (response != null) {
                return PriceDto.builder()
                        .exchange(ExchangeType.COINBASE)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(response.getBid()))
                        .askPrice(new BigDecimal(response.getAsk()))
                        .lastPrice(new BigDecimal(response.getPrice()))
                        .volume24h(response.getVolume() != null ? new BigDecimal(response.getVolume()) : null)
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from Coinbase for {}: {}", symbol, e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<PriceDto> fetchPrices(List<String> symbols) {
        List<PriceDto> prices = new ArrayList<>();
        for (String symbol : symbols) {
            PriceDto price = fetchPrice(symbol);
            if (price != null) {
                prices.add(price);
            }
        }
        return prices;
    }
    
    @Override
    public List<PriceDto> fetchAllTickers() {
        try {
            List<CoinbaseProductResponse> products = coinbaseFeignClient.getProducts();
            
            List<PriceDto> allPrices = new ArrayList<>();
            if (products != null) {
                for (CoinbaseProductResponse product : products) {
                    if (product.getStatus().equals("online")) {
                        PriceDto price = fetchPrice(product.getId());
                        if (price != null) {
                            allPrices.add(price);
                        }
                    }
                }
            }
            return allPrices;
        } catch (Exception e) {
            log.error("Error fetching all tickers from Coinbase: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
    
    @Override
    protected String convertToExchangeSymbol(String symbol) {
        // Convert BTC/USDT to BTC-USDT
        return symbol.replace("/", "-");
    }
    
    @Override
    protected String convertFromExchangeSymbol(String exchangeSymbol) {
        // Convert BTC-USDT to BTC/USDT
        return exchangeSymbol.replace("-", "/");
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoinbaseTickerResponse {
        private String bid;
        private String ask;
        private String price;
        private String volume;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoinbaseProductResponse {
        private String id;
        private String status;
        
        @JsonProperty("base_currency")
        private String baseCurrency;
        
        @JsonProperty("quote_currency")
        private String quoteCurrency;
    }
}

