package com.example.aram.client.feign;

import com.example.aram.client.impl.BybitClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for Bybit API
 * API Documentation: https://bybit-exchange.github.io/docs/
 */
@FeignClient(
    name = "bybit-client",
    url = "https://api.bybit.com",
    configuration = BybitFeignConfig.class
)
public interface BybitFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param category The category (spot, linear, inverse)
     * @param symbol The trading pair symbol (e.g., BTCUSDT, ETHUSDT)
     * @return BybitTickerResponse containing ticker data
     */
    @GetMapping("/v5/market/tickers")
    BybitClient.BybitTickerResponse getTicker(@RequestParam("category") String category, 
                                             @RequestParam("symbol") String symbol);
    
    /**
     * Get ticker information for all trading pairs in a category
     * @param category The category (spot, linear, inverse)
     * @return BybitTickerResponse containing all ticker data
     */
    @GetMapping("/v5/market/tickers")
    BybitClient.BybitTickerResponse getAllTickers(@RequestParam("category") String category);
}
