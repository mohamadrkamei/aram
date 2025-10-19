package com.example.aram.model;

import com.example.aram.enums.ExchangeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchanges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ExchangeType exchangeType;
    
    @Column(nullable = false)
    private String apiKey;
    
    @Column(nullable = false)
    private String apiSecret;
    
    @Column(nullable = false)
    private Boolean enabled;
    
    @Column(name = "fee_percentage")
    private Double feePercentage; // Trading fee percentage
    
    @Column(name = "withdrawal_fee")
    private Double withdrawalFee;
    
    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;
    
    @Column(name = "is_healthy")
    private Boolean isHealthy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enabled == null) {
            enabled = true;
        }
        if (isHealthy == null) {
            isHealthy = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

