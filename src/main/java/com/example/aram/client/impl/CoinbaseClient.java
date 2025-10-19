package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
    
    private static final String BASE_URL = "https://api.exchange.coinbase.com";
    
    public CoinbaseClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, BASE_URL, ExchangeType.COINBASE);
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String coinbaseSymbol = convertToExchangeSymbol(symbol);
            
            CoinbaseTickerResponse response = webClient
                    .get()
                    .uri("/products/{symbol}/ticker", coinbaseSymbol)
                    .retrieve()
                    .bodyToMono(CoinbaseTickerResponse.class)
                    .block();
            
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
            List<CoinbaseProductResponse> products = webClient
                    .get()
                    .uri("/products")
                    .retrieve()
                    .bodyToFlux(CoinbaseProductResponse.class)
                    .collectList()
                    .block();
            
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

