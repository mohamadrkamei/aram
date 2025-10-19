package com.example.aram.client.feign;

import com.example.aram.client.impl.KrakenClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for Kraken API
 * API Documentation: https://docs.kraken.com/rest/
 */
@FeignClient(
    name = "kraken-client",
    url = "https://api.kraken.com",
    configuration = KrakenFeignConfig.class
)
public interface KrakenFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param pair The trading pair symbol (e.g., XBTUSDT, ETHUSDT)
     * @return KrakenTickerResponse containing ticker data
     */
    @GetMapping("/0/public/Ticker")
    KrakenClient.KrakenTickerResponse getTicker(@RequestParam("pair") String pair);
}
