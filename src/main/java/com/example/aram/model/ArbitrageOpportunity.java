package com.example.aram.model;

import com.example.aram.enums.ArbitrageType;
import com.example.aram.enums.ExchangeType;
import com.example.aram.enums.OpportunityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "arbitrage_opportunities", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbitrageOpportunity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArbitrageType arbitrageType;
    
    @Column(nullable = false)
    private String symbol;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType buyExchange;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType sellExchange;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal buyPrice;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal sellPrice;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal profitPercentage;
    
    @Column(precision = 20, scale = 8)
    private BigDecimal estimatedProfit;
    
    @Column(precision = 20, scale = 8)
    private BigDecimal volume;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpportunityStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String executionDetails;
    
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OpportunityStatus.DETECTED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

