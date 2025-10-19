package com.example.aram.controller;

import com.example.aram.client.ExchangeClient;
import com.example.aram.dto.PriceDto;
import com.example.aram.service.ExchangeService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for comparing prices across exchanges
 */
@RestController
@RequestMapping("/api/price-comparison")
@RequiredArgsConstructor
public class PriceComparisonController {
    
    private final ExchangeService exchangeService;
    
    /**
     * Compare prices for a symbol across all exchanges
     */
    @GetMapping("/compare/{symbol}")
    public ResponseEntity<PriceComparisonResponse> comparePrices(@PathVariable String symbol) {
        List<PriceDto> prices = exchangeService.fetchPriceFromAllExchanges(symbol);
        
        if (prices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Find best buy and sell prices
        PriceDto bestBuy = prices.stream()
                .filter(p -> p.getAskPrice() != null)
                .min(Comparator.comparing(PriceDto::getAskPrice))
                .orElse(null);
        
        PriceDto bestSell = prices.stream()
                .filter(p -> p.getBidPrice() != null)
                .max(Comparator.comparing(PriceDto::getBidPrice))
                .orElse(null);
        
        // Calculate arbitrage opportunity
        BigDecimal arbitrageProfit = null;
        BigDecimal arbitrageProfitPercentage = null;
        
        if (bestBuy != null && bestSell != null && 
            bestSell.getBidPrice().compareTo(bestBuy.getAskPrice()) > 0) {
            arbitrageProfit = bestSell.getBidPrice().subtract(bestBuy.getAskPrice());
            arbitrageProfitPercentage = arbitrageProfit
                    .divide(bestBuy.getAskPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        PriceComparisonResponse response = PriceComparisonResponse.builder()
                .symbol(symbol)
                .exchangeCount(prices.size())
                .prices(prices)
                .bestBuyExchange(bestBuy != null ? bestBuy.getExchange().name() : null)
                .bestBuyPrice(bestBuy != null ? bestBuy.getAskPrice() : null)
                .bestSellExchange(bestSell != null ? bestSell.getExchange().name() : null)
                .bestSellPrice(bestSell != null ? bestSell.getBidPrice() : null)
                .arbitrageProfit(arbitrageProfit)
                .arbitrageProfitPercentage(arbitrageProfitPercentage)
                .hasArbitrageOpportunity(arbitrageProfitPercentage != null && 
                                        arbitrageProfitPercentage.compareTo(BigDecimal.ZERO) > 0)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get available exchanges
     */
    @GetMapping("/exchanges")
    public ResponseEntity<List<ExchangeInfo>> getAvailableExchanges() {
        List<ExchangeClient> clients = exchangeService.getAllClients();
        
        List<ExchangeInfo> exchanges = clients.stream()
                .map(client -> ExchangeInfo.builder()
                        .name(client.getExchangeType().name())
                        .displayName(client.getExchangeType().getDisplayName())
                        .healthy(client.isHealthy())
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(exchanges);
    }
    
    /**
     * Compare prices across multiple symbols
     */
    @PostMapping("/compare-multiple")
    public ResponseEntity<Map<String, PriceComparisonResponse>> compareMultipleSymbols(
            @RequestBody List<String> symbols) {
        
        Map<String, PriceComparisonResponse> results = new HashMap<>();
        
        for (String symbol : symbols) {
            List<PriceDto> prices = exchangeService.fetchPriceFromAllExchanges(symbol);
            
            if (!prices.isEmpty()) {
                PriceDto bestBuy = prices.stream()
                        .filter(p -> p.getAskPrice() != null)
                        .min(Comparator.comparing(PriceDto::getAskPrice))
                        .orElse(null);
                
                PriceDto bestSell = prices.stream()
                        .filter(p -> p.getBidPrice() != null)
                        .max(Comparator.comparing(PriceDto::getBidPrice))
                        .orElse(null);
                
                BigDecimal arbitrageProfit = null;
                BigDecimal arbitrageProfitPercentage = null;
                
                if (bestBuy != null && bestSell != null && 
                    bestSell.getBidPrice().compareTo(bestBuy.getAskPrice()) > 0) {
                    arbitrageProfit = bestSell.getBidPrice().subtract(bestBuy.getAskPrice());
                    arbitrageProfitPercentage = arbitrageProfit
                            .divide(bestBuy.getAskPrice(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }
                
                PriceComparisonResponse response = PriceComparisonResponse.builder()
                        .symbol(symbol)
                        .exchangeCount(prices.size())
                        .prices(prices)
                        .bestBuyExchange(bestBuy != null ? bestBuy.getExchange().name() : null)
                        .bestBuyPrice(bestBuy != null ? bestBuy.getAskPrice() : null)
                        .bestSellExchange(bestSell != null ? bestSell.getExchange().name() : null)
                        .bestSellPrice(bestSell != null ? bestSell.getBidPrice() : null)
                        .arbitrageProfit(arbitrageProfit)
                        .arbitrageProfitPercentage(arbitrageProfitPercentage)
                        .hasArbitrageOpportunity(arbitrageProfitPercentage != null && 
                                                arbitrageProfitPercentage.compareTo(BigDecimal.ZERO) > 0)
                        .build();
                
                results.put(symbol, response);
            }
        }
        
        return ResponseEntity.ok(results);
    }
    
    @Data
    @Builder
    public static class PriceComparisonResponse {
        private String symbol;
        private Integer exchangeCount;
        private List<PriceDto> prices;
        private String bestBuyExchange;
        private BigDecimal bestBuyPrice;
        private String bestSellExchange;
        private BigDecimal bestSellPrice;
        private BigDecimal arbitrageProfit;
        private BigDecimal arbitrageProfitPercentage;
        private Boolean hasArbitrageOpportunity;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    public static class ExchangeInfo {
        private String name;
        private String displayName;
        private Boolean healthy;
    }
}


