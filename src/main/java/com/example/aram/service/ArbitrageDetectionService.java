package com.example.aram.service;

import com.example.aram.enums.ArbitrageType;
import com.example.aram.enums.OpportunityStatus;
import com.example.aram.model.ArbitrageOpportunity;
import com.example.aram.model.Price;
import com.example.aram.repository.ArbitrageOpportunityRepository;
import com.example.aram.util.ArbitrageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArbitrageDetectionService {
    
    private final PriceService priceService;
    private final ArbitrageOpportunityRepository opportunityRepository;
    private final ArbitrageCalculator arbitrageCalculator;
    
    /**
     * Detect simple arbitrage opportunities for a symbol
     * Buy on exchange A, sell on exchange B
     */
    @Transactional
    public List<ArbitrageOpportunity> detectSimpleArbitrage(String symbol, BigDecimal minProfitPercentage) {
        List<ArbitrageOpportunity> opportunities = new ArrayList<>();
        List<Price> prices = priceService.getLatestPricesForSymbol(symbol);
        
        if (prices.size() < 2) {
            log.debug("Not enough price data for {} to detect arbitrage", symbol);
            return opportunities;
        }
        
        // Compare prices across all exchange pairs
        for (int i = 0; i < prices.size(); i++) {
            for (int j = i + 1; j < prices.size(); j++) {
                Price price1 = prices.get(i);
                Price price2 = prices.get(j);
                
                // Check if we can buy on exchange1 and sell on exchange2
                ArbitrageOpportunity opp1 = checkArbitrageOpportunity(
                        symbol, price1, price2, minProfitPercentage);
                if (opp1 != null) {
                    opportunities.add(opp1);
                }
                
                // Check if we can buy on exchange2 and sell on exchange1
                ArbitrageOpportunity opp2 = checkArbitrageOpportunity(
                        symbol, price2, price1, minProfitPercentage);
                if (opp2 != null) {
                    opportunities.add(opp2);
                }
            }
        }
        
        // Save all detected opportunities
        if (!opportunities.isEmpty()) {
            opportunityRepository.saveAll(opportunities);
            log.info("Detected {} arbitrage opportunities for {}", opportunities.size(), symbol);
        }
        
        return opportunities;
    }
    
    private ArbitrageOpportunity checkArbitrageOpportunity(
            String symbol, Price buyPrice, Price sellPrice, BigDecimal minProfitPercentage) {
        
        // Use ask price to buy, bid price to sell
        BigDecimal buyAt = buyPrice.getAskPrice();
        BigDecimal sellAt = sellPrice.getBidPrice();
        
        if (buyAt == null || sellAt == null) {
            return null;
        }
        
        BigDecimal profitPercentage = arbitrageCalculator.calculateProfitPercentage(buyAt, sellAt);
        
        if (profitPercentage.compareTo(minProfitPercentage) >= 0) {
            BigDecimal estimatedProfit = arbitrageCalculator.calculateEstimatedProfit(
                    buyAt, sellAt, BigDecimal.valueOf(1000)); // Example amount
            
            return ArbitrageOpportunity.builder()
                    .arbitrageType(ArbitrageType.SIMPLE)
                    .symbol(symbol)
                    .buyExchange(buyPrice.getExchangeType())
                    .sellExchange(sellPrice.getExchangeType())
                    .buyPrice(buyAt)
                    .sellPrice(sellAt)
                    .profitPercentage(profitPercentage)
                    .estimatedProfit(estimatedProfit)
                    .status(OpportunityStatus.DETECTED)
                    .build();
        }
        
        return null;
    }
    
    /**
     * Get all detected opportunities above a certain profit threshold
     */
    public List<ArbitrageOpportunity> getProfitableOpportunities(BigDecimal minProfit) {
        return opportunityRepository.findProfitableOpportunities(OpportunityStatus.DETECTED, minProfit);
    }
    
    /**
     * Get opportunity by ID
     */
    public ArbitrageOpportunity getOpportunity(Long id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found: " + id));
    }
}

