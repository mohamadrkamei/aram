package com.example.aram.model;

import com.example.aram.enums.ExchangeType;
import com.example.aram.enums.OrderSide;
import com.example.aram.enums.OrderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private ArbitrageOpportunity opportunity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType exchangeType;
    
    @Column(nullable = false)
    private String symbol;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide orderSide;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal price;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;
    
    @Column(precision = 20, scale = 8)
    private BigDecimal executedQuantity;
    
    @Column(precision = 20, scale = 8)
    private BigDecimal fee;
    
    @Column
    private String externalOrderId;
    
    @Column(nullable = false)
    private String status; // NEW, FILLED, PARTIALLY_FILLED, CANCELLED, etc.
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

