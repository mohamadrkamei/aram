package com.example.aram.client.feign;

import com.example.aram.client.impl.CoinbaseClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client interface for Coinbase Pro API
 * API Documentation: https://docs.cloud.coinbase.com/exchange/reference
 */
@FeignClient(
    name = "coinbase-client",
    url = "https://api.exchange.coinbase.com",
    configuration = CoinbaseFeignConfig.class
)
public interface CoinbaseFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param symbol The trading pair symbol (e.g., BTC-USD, ETH-USD)
     * @return CoinbaseTickerResponse containing ticker data
     */
    @GetMapping("/products/{symbol}/ticker")
    CoinbaseClient.CoinbaseTickerResponse getTicker(@PathVariable("symbol") String symbol);
    
    /**
     * Get all available products
     * @return List of CoinbaseProductResponse containing all products
     */
    @GetMapping("/products")
    List<CoinbaseClient.CoinbaseProductResponse> getProducts();
}
