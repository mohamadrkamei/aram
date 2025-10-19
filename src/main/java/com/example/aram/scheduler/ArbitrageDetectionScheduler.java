package com.example.aram.scheduler;

import com.example.aram.config.ArbitrageConfig;
import com.example.aram.model.ArbitrageOpportunity;
import com.example.aram.service.ArbitrageDetectionService;
import com.example.aram.service.TradeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArbitrageDetectionScheduler {
    
    private final ArbitrageDetectionService detectionService;
    private final TradeExecutionService executionService;
    private final ArbitrageConfig arbitrageConfig;
    
    /**
     * Continuously scan for arbitrage opportunities
     */
    @Scheduled(fixedDelayString = "${arbitrage.priceUpdateInterval:5000}")
    public void scanForOpportunities() {
        try {
            log.debug("Scanning for arbitrage opportunities");
            
            for (String pair : arbitrageConfig.getWatchedPairs()) {
                List<ArbitrageOpportunity> opportunities = 
                        detectionService.detectSimpleArbitrage(pair, arbitrageConfig.getMinProfitPercentage());
                
                if (!opportunities.isEmpty()) {
                    log.info("Found {} opportunities for {}", opportunities.size(), pair);
                    
                    // Auto-execute if enabled
                    if (arbitrageConfig.getAutoExecute()) {
                        executeOpportunities(opportunities);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error scanning for opportunities: {}", e.getMessage());
        }
    }
    
    private void executeOpportunities(List<ArbitrageOpportunity> opportunities) {
        for (ArbitrageOpportunity opportunity : opportunities) {
            try {
                // Execute with a default amount (this should be calculated based on available balance)
                BigDecimal amount = BigDecimal.valueOf(0.01);
                executionService.executeArbitrage(opportunity.getId(), amount);
            } catch (Exception e) {
                log.error("Error executing opportunity {}: {}", opportunity.getId(), e.getMessage());
            }
        }
    }
}

