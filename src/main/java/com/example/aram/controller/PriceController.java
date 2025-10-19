package com.example.aram.controller;

import com.example.aram.dto.PriceDto;
import com.example.aram.enums.ExchangeType;
import com.example.aram.model.Price;
import com.example.aram.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {
    
    private final PriceService priceService;
    
    /**
     * Get latest price for a symbol on a specific exchange
     */
    @GetMapping("/{exchange}/{symbol}")
    public ResponseEntity<PriceDto> getPrice(
            @PathVariable ExchangeType exchange,
            @PathVariable String symbol) {
        
        Price price = priceService.getLatestPrice(exchange, symbol);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToDto(price));
    }
    
    /**
     * Get latest prices for a symbol across all exchanges
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<List<PriceDto>> getPricesForSymbol(@PathVariable String symbol) {
        List<Price> prices = priceService.getLatestPricesForSymbol(symbol);
        
        List<PriceDto> dtos = prices.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Manually trigger price update for specific symbols
     */
    @PostMapping("/update")
    public ResponseEntity<String> updatePrices(@RequestBody List<String> symbols) {
        priceService.updatePrices(symbols);
        return ResponseEntity.ok("Price update initiated");
    }
    
    private PriceDto convertToDto(Price price) {
        return PriceDto.builder()
                .exchange(price.getExchangeType())
                .symbol(price.getSymbol())
                .bidPrice(price.getBidPrice())
                .askPrice(price.getAskPrice())
                .lastPrice(price.getLastPrice())
                .volume24h(price.getVolume24h())
                .timestamp(price.getTimestamp())
                .build();
    }
}

