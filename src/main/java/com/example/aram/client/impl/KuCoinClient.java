package com.example.aram.client.impl;

import com.example.aram.client.BaseExchangeClient;
import com.example.aram.client.feign.KuCoinFeignClient;
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
 * KuCoin API Client
 * API Documentation: https://docs.kucoin.com/
 */
@Component
@Slf4j
public class KuCoinClient extends BaseExchangeClient {
    
    private final KuCoinFeignClient kuCoinFeignClient;
    
    public KuCoinClient(KuCoinFeignClient kuCoinFeignClient) {
        super(null, null, ExchangeType.KUCOIN);
        this.kuCoinFeignClient = kuCoinFeignClient;
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        try {
            String kucoinSymbol = convertToExchangeSymbol(symbol);
            
            KuCoinTickerResponse response = kuCoinFeignClient.getTicker(kucoinSymbol);
            
            if (response != null && response.getData() != null) {
                KuCoinTickerData data = response.getData();
                return PriceDto.builder()
                        .exchange(ExchangeType.KUCOIN)
                        .symbol(symbol)
                        .bidPrice(new BigDecimal(data.getBestBid()))
                        .askPrice(new BigDecimal(data.getBestAsk()))
                        .lastPrice(new BigDecimal(data.getPrice()))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching price from KuCoin for {}: {}", symbol, e.getMessage());
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
            KuCoinAllTickersResponse response = kuCoinFeignClient.getAllTickers();
            
            if (response != null && response.getData() != null && response.getData().getTicker() != null) {
                return response.getData().getTicker().stream()
                        .map(ticker -> PriceDto.builder()
                                .exchange(ExchangeType.KUCOIN)
                                .symbol(convertFromExchangeSymbol(ticker.getSymbol()))
                                .bidPrice(ticker.getBuy() != null ? new BigDecimal(ticker.getBuy()) : null)
                                .askPrice(ticker.getSell() != null ? new BigDecimal(ticker.getSell()) : null)
                                .lastPrice(ticker.getLast() != null ? new BigDecimal(ticker.getLast()) : null)
                                .volume24h(ticker.getVol() != null ? new BigDecimal(ticker.getVol()) : null)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error fetching all tickers from KuCoin: {}", e.getMessage());
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
    public static class KuCoinTickerResponse {
        private String code;
        private KuCoinTickerData data;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KuCoinTickerData {
        private String bestBid;
        private String bestAsk;
        private String price;
        private String size;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KuCoinAllTickersResponse {
        private String code;
        private KuCoinAllTickersData data;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KuCoinAllTickersData {
        private Long time;
        private List<KuCoinTickerItem> ticker;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KuCoinTickerItem {
        private String symbol;
        private String buy;
        private String sell;
        private String last;
        private String vol;
    }
}

