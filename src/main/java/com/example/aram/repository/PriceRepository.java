package com.example.aram.repository;

import com.example.aram.enums.ExchangeType;
import com.example.aram.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    
    @Query("SELECT p FROM Price p WHERE p.exchangeType = :exchange AND p.symbol = :symbol " +
           "ORDER BY p.timestamp DESC LIMIT 1")
    Optional<Price> findLatestByExchangeAndSymbol(
        @Param("exchange") ExchangeType exchange, 
        @Param("symbol") String symbol
    );
    
    List<Price> findBySymbolAndTimestampAfter(String symbol, LocalDateTime timestamp);
    
    @Query("SELECT p FROM Price p WHERE p.symbol = :symbol AND p.timestamp = " +
           "(SELECT MAX(p2.timestamp) FROM Price p2 WHERE p2.symbol = :symbol AND p2.exchangeType = p.exchangeType)")
    List<Price> findLatestPricesForSymbol(@Param("symbol") String symbol);
    
    void deleteByTimestampBefore(LocalDateTime timestamp);
}

