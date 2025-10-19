package com.example.aram.controller;

import com.example.aram.dto.ArbitrageOpportunityDto;
import com.example.aram.model.ArbitrageOpportunity;
import com.example.aram.service.ArbitrageDetectionService;
import com.example.aram.service.TradeExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/arbitrage")
@RequiredArgsConstructor
public class ArbitrageController {
    
    private final ArbitrageDetectionService detectionService;
    private final TradeExecutionService executionService;
    
    /**
     * Detect arbitrage opportunities for a symbol
     */
    @PostMapping("/detect")
    public ResponseEntity<List<ArbitrageOpportunityDto>> detectOpportunities(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "0.5") BigDecimal minProfitPercentage) {
        
        List<ArbitrageOpportunity> opportunities = 
                detectionService.detectSimpleArbitrage(symbol, minProfitPercentage);
        
        List<ArbitrageOpportunityDto> dtos = opportunities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get all profitable opportunities
     */
    @GetMapping("/opportunities")
    public ResponseEntity<List<ArbitrageOpportunityDto>> getOpportunities(
            @RequestParam(defaultValue = "0.5") BigDecimal minProfit) {
        
        List<ArbitrageOpportunity> opportunities = 
                detectionService.getProfitableOpportunities(minProfit);
        
        List<ArbitrageOpportunityDto> dtos = opportunities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get a specific opportunity
     */
    @GetMapping("/opportunities/{id}")
    public ResponseEntity<ArbitrageOpportunityDto> getOpportunity(@PathVariable Long id) {
        ArbitrageOpportunity opportunity = detectionService.getOpportunity(id);
        return ResponseEntity.ok(convertToDto(opportunity));
    }
    
    /**
     * Execute an arbitrage opportunity
     */
    @PostMapping("/execute/{opportunityId}")
    public ResponseEntity<String> executeArbitrage(
            @PathVariable Long opportunityId,
            @RequestParam BigDecimal amount) {
        
        executionService.executeArbitrage(opportunityId, amount);
        return ResponseEntity.ok("Arbitrage execution initiated");
    }
    
    private ArbitrageOpportunityDto convertToDto(ArbitrageOpportunity opportunity) {
        return ArbitrageOpportunityDto.builder()
                .id(opportunity.getId())
                .type(opportunity.getArbitrageType())
                .symbol(opportunity.getSymbol())
                .buyExchange(opportunity.getBuyExchange())
                .sellExchange(opportunity.getSellExchange())
                .buyPrice(opportunity.getBuyPrice())
                .sellPrice(opportunity.getSellPrice())
                .profitPercentage(opportunity.getProfitPercentage())
                .estimatedProfit(opportunity.getEstimatedProfit())
                .volume(opportunity.getVolume())
                .status(opportunity.getStatus())
                .detectedAt(opportunity.getDetectedAt())
                .build();
    }
}

