package com.example.aram.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ArbitrageCalculator {
    
    /**
     * Calculate profit percentage between buy and sell prices
     */
    public BigDecimal calculateProfitPercentage(BigDecimal buyPrice, BigDecimal sellPrice) {
        if (buyPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = sellPrice.subtract(buyPrice);
        return profit.divide(buyPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Calculate estimated profit for a given amount
     */
    public BigDecimal calculateEstimatedProfit(BigDecimal buyPrice, BigDecimal sellPrice, BigDecimal amount) {
        BigDecimal buyTotal = buyPrice.multiply(amount);
        BigDecimal sellTotal = sellPrice.multiply(amount);
        return sellTotal.subtract(buyTotal);
    }
    
    /**
     * Calculate profit after fees
     */
    public BigDecimal calculateProfitAfterFees(BigDecimal buyPrice, BigDecimal sellPrice, 
                                               BigDecimal amount, BigDecimal buyFee, BigDecimal sellFee) {
        BigDecimal buyTotal = buyPrice.multiply(amount).multiply(BigDecimal.ONE.add(buyFee));
        BigDecimal sellTotal = sellPrice.multiply(amount).multiply(BigDecimal.ONE.subtract(sellFee));
        return sellTotal.subtract(buyTotal);
    }
    
    /**
     * Calculate optimal trade amount based on available balance and market depth
     */
    public BigDecimal calculateOptimalAmount(BigDecimal balance, BigDecimal price, 
                                            BigDecimal maxPercentageOfBalance) {
        BigDecimal maxAmount = balance.multiply(maxPercentageOfBalance);
        return maxAmount.divide(price, 8, RoundingMode.DOWN);
    }
}

