package com.example.aram.repository;

import com.example.aram.enums.ExchangeType;
import com.example.aram.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    
    Optional<Exchange> findByExchangeType(ExchangeType exchangeType);
    
    List<Exchange> findByEnabledTrue();
    
    List<Exchange> findByEnabledTrueAndIsHealthyTrue();
}

