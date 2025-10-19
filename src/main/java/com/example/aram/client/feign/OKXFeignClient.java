package com.example.aram.client.feign;

import com.example.aram.client.impl.OKXClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for OKX API
 * API Documentation: https://www.okx.com/docs-v5/en/
 */
@FeignClient(
    name = "okx-client",
    url = "https://www.okx.com",
    configuration = OKXFeignConfig.class
)
public interface OKXFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param instId The trading pair symbol (e.g., BTC-USDT, ETH-USDT)
     * @return OKXTickerResponse containing ticker data
     */
    @GetMapping("/api/v5/market/ticker")
    OKXClient.OKXTickerResponse getTicker(@RequestParam("instId") String instId);
    
    /**
     * Get ticker information for all trading pairs
     * @param instType The instrument type (SPOT, FUTURES, etc.)
     * @return OKXTickerResponse containing all ticker data
     */
    @GetMapping("/api/v5/market/tickers")
    OKXClient.OKXTickerResponse getAllTickers(@RequestParam("instType") String instType);
}
