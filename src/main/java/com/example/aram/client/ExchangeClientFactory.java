package com.example.aram.client;

import com.example.aram.enums.ExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory to manage and provide exchange clients
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ExchangeClientFactory {
    
    private final List<ExchangeClient> exchangeClients;
    private Map<ExchangeType, ExchangeClient> clientMap;
    
    /**
     * Initialize the client map
     */
    public void init() {
        clientMap = exchangeClients.stream()
                .collect(Collectors.toMap(
                        ExchangeClient::getExchangeType,
                        Function.identity()
                ));
        log.info("Initialized {} exchange clients", clientMap.size());
    }
    
    /**
     * Get client for specific exchange
     */
    public Optional<ExchangeClient> getClient(ExchangeType exchangeType) {
        if (clientMap == null) {
            init();
        }
        return Optional.ofNullable(clientMap.get(exchangeType));
    }
    
    /**
     * Get all available clients
     */
    public List<ExchangeClient> getAllClients() {
        if (clientMap == null) {
            init();
        }
        return List.copyOf(clientMap.values());
    }
    
    /**
     * Get all healthy clients
     */
    public List<ExchangeClient> getHealthyClients() {
        return getAllClients().stream()
                .filter(ExchangeClient::isHealthy)
                .collect(Collectors.toList());
    }
}


