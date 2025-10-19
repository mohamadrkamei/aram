package com.example.aram.controller;

import com.example.aram.enums.ExchangeType;
import com.example.aram.model.Exchange;
import com.example.aram.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeService exchangeService;
    
    /**
     * Get all active exchanges
     */
    @GetMapping
    public ResponseEntity<List<Exchange>> getActiveExchanges() {
        List<Exchange> exchanges = exchangeService.getAllActiveExchanges();
        return ResponseEntity.ok(exchanges);
    }
    
    /**
     * Get a specific exchange
     */
    @GetMapping("/{exchangeType}")
    public ResponseEntity<Exchange> getExchange(@PathVariable ExchangeType exchangeType) {
        Exchange exchange = exchangeService.getExchange(exchangeType);
        return ResponseEntity.ok(exchange);
    }
    
    /**
     * Check exchange health
     */
    @GetMapping("/{exchangeType}/health")
    public ResponseEntity<Boolean> checkHealth(@PathVariable ExchangeType exchangeType) {
        boolean healthy = exchangeService.checkHealth(exchangeType);
        return ResponseEntity.ok(healthy);
    }
}

