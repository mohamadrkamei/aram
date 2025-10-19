package com.example.aram.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "arbitrage")
@Data
public class ArbitrageConfig {
    
    /**
     * Minimum profit percentage to consider an opportunity
     */
    private BigDecimal minProfitPercentage = BigDecimal.valueOf(0.5);
    
    /**
     * Maximum percentage of balance to use per trade
     */
    private BigDecimal maxBalancePercentage = BigDecimal.valueOf(0.1);
    
    /**
     * List of trading pairs to monitor
     */
    private List<String> watchedPairs = List.of("BTC/USDT", "ETH/USDT", "BNB/USDT");
    
    /**
     * Price update interval in milliseconds
     */
    private Long priceUpdateInterval = 5000L;
    
    /**
     * Enable automatic trade execution
     */
    private Boolean autoExecute = false;
    
    /**
     * Number of days to keep price history
     */
    private Integer priceHistoryDays = 7;
}

