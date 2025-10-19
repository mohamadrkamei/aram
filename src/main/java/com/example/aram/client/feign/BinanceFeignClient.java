package com.example.aram.client.feign;

import com.example.aram.client.impl.BinanceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client interface for Binance API
 * API Documentation: https://binance-docs.github.io/apidocs/spot/en/
 */
@FeignClient(
    name = "binance-client",
    url = "https://api.binance.com",
    configuration = BinanceFeignConfig.class
)
public interface BinanceFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param symbol The trading pair symbol (e.g., BTCUSDT, ETHUSDT)
     * @return BinanceTickerResponse containing ticker data
     */
    @GetMapping("/api/v3/ticker/bookTicker")
    BinanceClient.BinanceTickerResponse getTicker(@RequestParam("symbol") String symbol);
    
    /**
     * Get ticker information for all trading pairs
     * @return List of BinanceTickerResponse containing all ticker data
     */
    @GetMapping("/api/v3/ticker/bookTicker")
    List<BinanceClient.BinanceTickerResponse> getAllTickers();
}
