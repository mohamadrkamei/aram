package com.example.aram.dto;

import com.example.aram.enums.ArbitrageType;
import com.example.aram.enums.ExchangeType;
import com.example.aram.enums.OpportunityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbitrageOpportunityDto {
    private Long id;
    private ArbitrageType type;
    private String symbol;
    private ExchangeType buyExchange;
    private ExchangeType sellExchange;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal profitPercentage;
    private BigDecimal estimatedProfit;
    private BigDecimal volume;
    private OpportunityStatus status;
    private LocalDateTime detectedAt;
}

