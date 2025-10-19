package com.example.aram.repository;

import com.example.aram.model.TradingPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradingPairRepository extends JpaRepository<TradingPair, Long> {
    
    Optional<TradingPair> findBySymbol(String symbol);
    
    List<TradingPair> findByActiveTrue();
    
    boolean existsBySymbol(String symbol);
}

