package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.client.feign.BybitFeignClient;
import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bybit API Client
 * API Documentation: https://bybit-exchange.github.io/docs/
 */
@Component
@Slf4j
public class BybitClient extends BaseExchangeClient {
    
    private final BybitFeignClient bybitFeignClient;
    
    public BybitClient(BybitFeignClient bybitFeignClient) {
        super(null, null, ExchangeType.BYBIT);
        this.bybitFeignClient = bybitFeignClient;
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String bybitSymbol = convertToExchangeSymbol(symbol);
            
            BybitTickerResponse response = bybitFeignClient.getTicker("spot", bybitSymbol);
            
            if (response != null && response.getResult() != null && 
                response.getResult().getList() != null && !response.getResult().getList().isEmpty()) {
                BybitTicker ticker = response.getResult().getList().get(0);
                
                return PriceDto.builder()
                        .exchange(ExchangeType.BYBIT)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(ticker.getBid1Price()))
                        .askPrice(new BigDecimal(ticker.getAsk1Price()))
                        .lastPrice(new BigDecimal(ticker.getLastPrice()))
                        .volume24h(new BigDecimal(ticker.getVolume24h()))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from Bybit for {}: {}", symbol, e.getMessage());
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
            BybitTickerResponse response = bybitFeignClient.getAllTickers("spot");
            
            if (response != null && response.getResult() != null && response.getResult().getList() != null) {
                return response.getResult().getList().stream()
                        .map(ticker -> PriceDto.builder()
                                .exchange(ExchangeType.BYBIT)
                                .symbol(convertFromExchangeSymbol(ticker.getSymbol()))
                                .bidPrice(new BigDecimal(ticker.getBid1Price()))
                                .askPrice(new BigDecimal(ticker.getAsk1Price()))
                                .lastPrice(new BigDecimal(ticker.getLastPrice()))
                                .volume24h(new BigDecimal(ticker.getVolume24h()))
                                .timestamp(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error fetching all tickers from Bybit: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
    
    @Override
    protected String convertToExchangeSymbol(String symbol) {
        // Convert BTC/USDT to BTCUSDT
        return symbol.replace("/", "");
    }
    
    @Override
    protected String convertFromExchangeSymbol(String exchangeSymbol) {
        // Convert BTCUSDT to BTC/USDT
        if (exchangeSymbol.endsWith("USDT")) {
            String base = exchangeSymbol.substring(0, exchangeSymbol.length() - 4);
            return base + "/USDT";
        }
        return exchangeSymbol;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BybitTickerResponse {
        private Integer retCode;
        private String retMsg;
        private BybitResult result;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BybitResult {
        private String category;
        private List<BybitTicker> list;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BybitTicker {
        private String symbol;
        private String lastPrice;
        private String bid1Price;
        private String ask1Price;
        private String volume24h;
    }
}


