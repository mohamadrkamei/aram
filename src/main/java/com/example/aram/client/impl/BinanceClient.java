package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.client.feign.BinanceFeignClient;
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
import java.util.stream.Collectors;

/**
 * Binance API Client
 * API Documentation: https://binance-docs.github.io/apidocs/spot/en/
 */
@Component
@Slf4j
public class BinanceClient extends BaseExchangeClient {
    
    private final BinanceFeignClient binanceFeignClient;
    
    public BinanceClient(BinanceFeignClient binanceFeignClient) {
        super(null, null, ExchangeType.BINANCE);
        this.binanceFeignClient = binanceFeignClient;
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String binanceSymbol = convertToExchangeSymbol(symbol);
            
            BinanceTickerResponse response = binanceFeignClient.getTicker(binanceSymbol);
            
            if (response != null) {
                return PriceDto.builder()
                        .exchange(ExchangeType.BINANCE)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(response.getBidPrice()))
                        .askPrice(new BigDecimal(response.getAskPrice()))
                        .lastPrice(new BigDecimal(response.getBidPrice()).add(new BigDecimal(response.getAskPrice())).divide(BigDecimal.valueOf(2)))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from Binance for {}: {}", symbol, e.getMessage());
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
            List<BinanceTickerResponse> responses = binanceFeignClient.getAllTickers();
            
            if (responses != null) {
                return responses.stream()
                        .map(response -> PriceDto.builder()
                                .exchange(ExchangeType.BINANCE)
                                .symbol(convertFromExchangeSymbol(response.getSymbol()))
                                .bidPrice(new BigDecimal(response.getBidPrice()))
                                .askPrice(new BigDecimal(response.getAskPrice()))
                                .lastPrice(new BigDecimal(response.getBidPrice()).add(new BigDecimal(response.getAskPrice())).divide(BigDecimal.valueOf(2)))
                                .timestamp(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error fetching all tickers from Binance: {}", e.getMessage());
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
        // This is a simplified version - you may need more sophisticated logic
        if (exchangeSymbol.endsWith("USDT")) {
            String base = exchangeSymbol.substring(0, exchangeSymbol.length() - 4);
            return base + "/USDT";
        }
        if (exchangeSymbol.endsWith("BTC")) {
            String base = exchangeSymbol.substring(0, exchangeSymbol.length() - 3);
            return base + "/BTC";
        }
        return exchangeSymbol;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BinanceTickerResponse {
        private String symbol;
        
        @JsonProperty("bidPrice")
        private String bidPrice;
        
        @JsonProperty("askPrice")
        private String askPrice;
        
        @JsonProperty("bidQty")
        private String bidQty;
        
        @JsonProperty("askQty")
        private String askQty;
    }
}

