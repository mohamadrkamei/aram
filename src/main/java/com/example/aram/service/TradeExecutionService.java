package com.example.aram.service;

import com.example.aram.enums.OpportunityStatus;
import com.example.aram.enums.OrderSide;
import com.example.aram.enums.OrderType;
import com.example.aram.model.ArbitrageOpportunity;
import com.example.aram.model.Trade;
import com.example.aram.repository.ArbitrageOpportunityRepository;
import com.example.aram.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeExecutionService {
    
    private final ExchangeService exchangeService;
    private final TradeRepository tradeRepository;
    private final ArbitrageOpportunityRepository opportunityRepository;
    
    /**
     * Execute an arbitrage opportunity
     */
    @Transactional
    public void executeArbitrage(Long opportunityId, BigDecimal amount) {
        ArbitrageOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new RuntimeException("Opportunity not found: " + opportunityId));
        
        if (opportunity.getStatus() != OpportunityStatus.DETECTED) {
            log.warn("Opportunity {} is not in DETECTED status", opportunityId);
            return;
        }
        
        try {
            // Update status to EXECUTING
            opportunity.setStatus(OpportunityStatus.EXECUTING);
            opportunity.setExecutedAt(LocalDateTime.now());
            opportunityRepository.save(opportunity);
            
            // Execute buy order
            Trade buyTrade = executeBuyOrder(opportunity, amount);
            
            // Execute sell order
            Trade sellTrade = executeSellOrder(opportunity, amount);
            
            // Update opportunity status
            opportunity.setStatus(OpportunityStatus.COMPLETED);
            opportunity.setCompletedAt(LocalDateTime.now());
            opportunity.setExecutionDetails(
                    String.format("Buy Order: %s, Sell Order: %s", 
                            buyTrade.getExternalOrderId(), sellTrade.getExternalOrderId()));
            opportunityRepository.save(opportunity);
            
            log.info("Successfully executed arbitrage opportunity {}", opportunityId);
            
        } catch (Exception e) {
            log.error("Failed to execute arbitrage opportunity {}: {}", opportunityId, e.getMessage());
            opportunity.setStatus(OpportunityStatus.FAILED);
            opportunity.setExecutionDetails("Error: " + e.getMessage());
            opportunityRepository.save(opportunity);
        }
    }
    
    private Trade executeBuyOrder(ArbitrageOpportunity opportunity, BigDecimal amount) {
        String orderId = exchangeService.executeTrade(
                opportunity.getBuyExchange(),
                opportunity.getSymbol(),
                OrderType.MARKET,
                OrderSide.BUY,
                opportunity.getBuyPrice(),
                amount
        );
        
        Trade trade = Trade.builder()
                .opportunity(opportunity)
                .exchangeType(opportunity.getBuyExchange())
                .symbol(opportunity.getSymbol())
                .orderType(OrderType.MARKET)
                .orderSide(OrderSide.BUY)
                .price(opportunity.getBuyPrice())
                .quantity(amount)
                .executedQuantity(amount)
                .externalOrderId(orderId)
                .status("FILLED")
                .executedAt(LocalDateTime.now())
                .build();
        
        return tradeRepository.save(trade);
    }
    
    private Trade executeSellOrder(ArbitrageOpportunity opportunity, BigDecimal amount) {
        String orderId = exchangeService.executeTrade(
                opportunity.getSellExchange(),
                opportunity.getSymbol(),
                OrderType.MARKET,
                OrderSide.SELL,
                opportunity.getSellPrice(),
                amount
        );
        
        Trade trade = Trade.builder()
                .opportunity(opportunity)
                .exchangeType(opportunity.getSellExchange())
                .symbol(opportunity.getSymbol())
                .orderType(OrderType.MARKET)
                .orderSide(OrderSide.SELL)
                .price(opportunity.getSellPrice())
                .quantity(amount)
                .executedQuantity(amount)
                .externalOrderId(orderId)
                .status("FILLED")
                .executedAt(LocalDateTime.now())
                .build();
        
        return tradeRepository.save(trade);
    }
}

