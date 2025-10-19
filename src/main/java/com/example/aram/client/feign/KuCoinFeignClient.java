package com.example.aram.client.feign;

import com.example.aram.client.impl.KuCoinClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for KuCoin API
 * API Documentation: https://docs.kucoin.com/
 */
@FeignClient(
    name = "kucoin-client",
    url = "https://api.kucoin.com",
    configuration = KuCoinFeignConfig.class
)
public interface KuCoinFeignClient {
    
    /**
     * Get ticker information for a specific trading pair
     * @param symbol The trading pair symbol (e.g., BTC-USDT, ETH-USDT)
     * @return KuCoinTickerResponse containing ticker data
     */
    @GetMapping("/api/v1/market/orderbook/level1")
    KuCoinClient.KuCoinTickerResponse getTicker(@RequestParam("symbol") String symbol);
    
    /**
     * Get all tickers
     * @return KuCoinAllTickersResponse containing all ticker data
     */
    @GetMapping("/api/v1/market/allTickers")
    KuCoinClient.KuCoinAllTickersResponse getAllTickers();
}
