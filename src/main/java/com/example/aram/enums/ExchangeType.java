package com.example.aram.enums;

public enum ExchangeType {
    BINANCE("Binance", "https://api.binance.com"),
    COINBASE("Coinbase", "https://api.coinbase.com"),
    KRAKEN("Kraken", "https://api.kraken.com"),
    KUCOIN("KuCoin", "https://api.kucoin.com"),
    BYBIT("Bybit", "https://api.bybit.com"),
    OKX("OKX", "https://www.okx.com");

    private final String displayName;
    private final String apiUrl;

    ExchangeType(String displayName, String apiUrl) {
        this.displayName = displayName;
        this.apiUrl = apiUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}

