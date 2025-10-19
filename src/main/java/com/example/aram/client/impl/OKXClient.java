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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OKX API Client
 * API Documentation: https://www.okx.com/docs-v5/en/
 */
@Component
@Slf4j
public class OKXClient extends BaseExchangeClient {
    
    private static final String BASE_URL = "https://www.okx.com";
    
    public OKXClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, BASE_URL, ExchangeType.OKX);
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String okxSymbol = convertToExchangeSymbol(symbol);
            
            OKXTickerResponse response = webClient
                    .get()
                    .uri("/api/v5/market/ticker?instId={symbol}", okxSymbol)
                    .retrieve()
                    .bodyToMono(OKXTickerResponse.class)
                    .block();
            
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                OKXTicker ticker = response.getData().get(0);
                
                return PriceDto.builder()
                        .exchange(ExchangeType.OKX)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(ticker.getBidPx()))
                        .askPrice(new BigDecimal(ticker.getAskPx()))
                        .lastPrice(new BigDecimal(ticker.getLast()))
                        .volume24h(new BigDecimal(ticker.getVol24h()))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from OKX for {}: {}", symbol, e.getMessage());
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
            OKXTickerResponse response = webClient
                    .get()
                    .uri("/api/v5/market/tickers?instType=SPOT")
                    .retrieve()
                    .bodyToMono(OKXTickerResponse.class)
                    .block();
            
            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .map(ticker -> PriceDto.builder()
                                .exchange(ExchangeType.OKX)
                                .symbol(convertFromExchangeSymbol(ticker.getInstId()))
                                .bidPrice(new BigDecimal(ticker.getBidPx()))
                                .askPrice(new BigDecimal(ticker.getAskPx()))
                                .lastPrice(new BigDecimal(ticker.getLast()))
                                .volume24h(new BigDecimal(ticker.getVol24h()))
                                .timestamp(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error fetching all tickers from OKX: {}", e.getMessage());
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
    public static class OKXTickerResponse {
        private String code;
        private String msg;
        private List<OKXTicker> data;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OKXTicker {
        private String instId;
        private String last;
        private String bidPx;
        private String askPx;
        private String vol24h;
    }
}


