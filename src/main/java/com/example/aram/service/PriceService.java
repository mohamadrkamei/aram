package com.example.aram.service;

import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.example.aram.model.Exchange;
import com.example.aram.model.Price;
import com.example.aram.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {
    
    private final PriceRepository priceRepository;
    private final ExchangeService exchangeService;
    
    /**
     * Fetch and store prices for all active exchanges and symbols
     */
    @Transactional
    public void updatePrices(List<String> symbols) {
        for (String symbol : symbols) {
            try {
                List<PriceDto> prices = exchangeService.fetchPriceFromAllExchanges(symbol);
                for (PriceDto priceDto : prices) {
                    if (priceDto != null) {
                        savePrice(priceDto);
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching prices for {}: {}", symbol, e.getMessage());
            }
        }
    }
    
    /**
     * Save price data to database
     */
    @Transactional
    public void savePrice(PriceDto priceDto) {
        Price price = Price.builder()
                .exchangeType(priceDto.getExchange())
                .symbol(priceDto.getSymbol())
                .bidPrice(priceDto.getBidPrice())
                .askPrice(priceDto.getAskPrice())
                .lastPrice(priceDto.getLastPrice())
                .volume24h(priceDto.getVolume24h())
                .timestamp(priceDto.getTimestamp() != null ? priceDto.getTimestamp() : LocalDateTime.now())
                .build();
        
        priceRepository.save(price);
    }
    
    /**
     * Get latest price for a symbol on a specific exchange
     */
    public Price getLatestPrice(ExchangeType exchange, String symbol) {
        return priceRepository.findLatestByExchangeAndSymbol(exchange, symbol)
                .orElse(null);
    }
    
    /**
     * Get latest prices for a symbol across all exchanges
     */
    public List<Price> getLatestPricesForSymbol(String symbol) {
        return priceRepository.findLatestPricesForSymbol(symbol);
    }
    
    /**
     * Clean up old price data
     */
    @Transactional
    public void cleanupOldPrices(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        priceRepository.deleteByTimestampBefore(cutoffDate);
        log.info("Cleaned up prices older than {}", cutoffDate);
    }
}

