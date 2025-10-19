package com.example.aram.repository;

import com.example.aram.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    List<Trade> findByOpportunityId(Long opportunityId);
    
    Optional<Trade> findByExternalOrderId(String externalOrderId);
    
    List<Trade> findByStatus(String status);
}

