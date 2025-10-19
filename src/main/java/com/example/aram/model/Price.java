package com.example.aram.model;

import com.example.aram.enums.ExchangeType;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices", indexes = {
    @Index(name = "idx_exchange_symbol", columnList = "exchange_type,symbol"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType exchangeType;
    
    @Column(nullable = false)
    private String symbol; // e.g., BTC/USDT
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal bidPrice;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal askPrice;
    
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal lastPrice;
    
    @Column(precision = 20, scale = 8)
    private BigDecimal volume24h;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}

