package com.example.aram.enums;

public enum ArbitrageType {
    SIMPLE,          // Buy on exchange A, sell on exchange B
    TRIANGULAR,      // Trade between 3 currencies on same exchange
    CROSS_EXCHANGE   // Complex multi-exchange arbitrage
}

