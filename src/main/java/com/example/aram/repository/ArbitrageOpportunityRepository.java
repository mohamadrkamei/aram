package com.example.aram.repository;

import com.example.aram.enums.OpportunityStatus;
import com.example.aram.model.ArbitrageOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArbitrageOpportunityRepository extends JpaRepository<ArbitrageOpportunity, Long> {
    
    List<ArbitrageOpportunity> findByStatus(OpportunityStatus status);
    
    List<ArbitrageOpportunity> findByStatusOrderByProfitPercentageDesc(OpportunityStatus status);
    
    List<ArbitrageOpportunity> findByDetectedAtAfter(LocalDateTime timestamp);
    
    @Query("SELECT a FROM ArbitrageOpportunity a WHERE a.status = :status " +
           "AND a.profitPercentage >= :minProfit ORDER BY a.profitPercentage DESC")
    List<ArbitrageOpportunity> findProfitableOpportunities(
        @Param("status") OpportunityStatus status,
        @Param("minProfit") java.math.BigDecimal minProfit
    );
    
    long countByStatusAndDetectedAtAfter(OpportunityStatus status, LocalDateTime timestamp);
}

