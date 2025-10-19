package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Kraken API Client
 * API Documentation: https://docs.kraken.com/rest/
 */
@Component
@Slf4j
public class KrakenClient extends BaseExchangeClient {
    
    private static final String BASE_URL = "https://api.kraken.com";
    
    // Kraken symbol mapping
    private static final Map<String, String> SYMBOL_MAP = new HashMap<>();
    static {
        SYMBOL_MAP.put("BTC/USDT", "XBTUSDT");
        SYMBOL_MAP.put("ETH/USDT", "ETHUSDT");
        SYMBOL_MAP.put("BTC/USD", "XBTUSD");
        SYMBOL_MAP.put("ETH/USD", "ETHUSD");
    }
    
    public KrakenClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, BASE_URL, ExchangeType.KRAKEN);
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String krakenSymbol = convertToExchangeSymbol(symbol);
            
            KrakenTickerResponse response = webClient
                    .get()
                    .uri("/0/public/Ticker?pair={symbol}", krakenSymbol)
                    .retrieve()
                    .bodyToMono(KrakenTickerResponse.class)
                    .block();
            
            if (response != null && response.getResult() != null && !response.getResult().isEmpty()) {
                // Kraken returns data with the pair name as key
                Map.Entry<String, KrakenTicker> entry = response.getResult().entrySet().iterator().next();
                KrakenTicker ticker = entry.getValue();
                
                return PriceDto.builder()
                        .exchange(ExchangeType.KRAKEN)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(ticker.getBid().get(0)))
                        .askPrice(new BigDecimal(ticker.getAsk().get(0)))
                        .lastPrice(new BigDecimal(ticker.getLastPrice().get(0)))
                        .volume24h(new BigDecimal(ticker.getVolume().get(1)))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from Kraken for {}: {}", symbol, e.getMessage());
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
        // Kraken doesn't have a single endpoint for all tickers in the same way
        // We'll fetch common pairs
        List<String> commonPairs = Arrays.asList("BTC/USDT", "ETH/USDT", "BTC/USD", "ETH/USD");
        return fetchPrices(commonPairs);
    }
    
    @Override
    protected String convertToExchangeSymbol(String symbol) {
        return SYMBOL_MAP.getOrDefault(symbol, symbol.replace("/", ""));
    }
    
    @Override
    protected String convertFromExchangeSymbol(String exchangeSymbol) {
        // Reverse lookup in symbol map
        for (Map.Entry<String, String> entry : SYMBOL_MAP.entrySet()) {
            if (entry.getValue().equals(exchangeSymbol)) {
                return entry.getKey();
            }
        }
        return exchangeSymbol;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KrakenTickerResponse {
        private List<String> error;
        private Map<String, KrakenTicker> result;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KrakenTicker {
        private List<String> bid;  // [price, whole lot volume, lot volume]
        private List<String> ask;
        
        @com.fasterxml.jackson.annotation.JsonProperty("c")
        private List<String> lastPrice;
        
        @com.fasterxml.jackson.annotation.JsonProperty("v")
        private List<String> volume;
    }
}

