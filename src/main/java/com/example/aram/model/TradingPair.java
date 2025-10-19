package com.example.aram.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trading_pairs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingPair {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol; // e.g., BTC/USDT
    
    @Column(nullable = false)
    private String baseCurrency; // e.g., BTC
    
    @Column(nullable = false)
    private String quoteCurrency; // e.g., USDT
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

