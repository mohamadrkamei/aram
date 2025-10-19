package com.example.aram.scheduler;

import com.example.aram.config.ArbitrageConfig;
import com.example.aram.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceUpdateScheduler {
    
    private final PriceService priceService;
    private final ArbitrageConfig arbitrageConfig;
    
    /**
     * Update prices at configured interval
     */
    @Scheduled(fixedDelayString = "${arbitrage.priceUpdateInterval:5000}")
    public void updatePrices() {
        try {
            log.debug("Updating prices for watched pairs");
            priceService.updatePrices(arbitrageConfig.getWatchedPairs());
        } catch (Exception e) {
            log.error("Error updating prices: {}", e.getMessage());
        }
    }
    
    /**
     * Clean up old price data daily
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM daily
    public void cleanupOldPrices() {
        try {
            log.info("Cleaning up old price data");
            priceService.cleanupOldPrices(arbitrageConfig.getPriceHistoryDays());
        } catch (Exception e) {
            log.error("Error cleaning up old prices: {}", e.getMessage());
        }
    }
}

