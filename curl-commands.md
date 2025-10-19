# Exchange API Curl Commands

This document contains curl commands for testing price endpoints from all supported exchanges.

## 1. Kraken API

### Get Single Ticker
```bash
curl -X GET "https://api.kraken.com/0/public/Ticker?pair=XBTUSDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get Multiple Tickers
```bash
curl -X GET "https://api.kraken.com/0/public/Ticker?pair=XBTUSDT,ETHUSDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## 2. Binance API

### Get Single Ticker (Book Ticker)
```bash
curl -X GET "https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Tickers
```bash
curl -X GET "https://api.binance.com/api/v3/ticker/bookTicker" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get 24hr Ticker Price Change Statistics
```bash
curl -X GET "https://api.binance.com/api/v3/ticker/24hr?symbol=BTCUSDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## 3. Bybit API

### Get Single Ticker (Spot)
```bash
curl -X GET "https://api.bybit.com/v5/market/tickers?category=spot&symbol=BTCUSDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Spot Tickers
```bash
curl -X GET "https://api.bybit.com/v5/market/tickers?category=spot" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get Linear Tickers
```bash
curl -X GET "https://api.bybit.com/v5/market/tickers?category=linear" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## 4. Coinbase Pro API

### Get Single Ticker
```bash
curl -X GET "https://api.exchange.coinbase.com/products/BTC-USD/ticker" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Products
```bash
curl -X GET "https://api.exchange.coinbase.com/products" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get Product Order Book
```bash
curl -X GET "https://api.exchange.coinbase.com/products/BTC-USD/book?level=1" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## 5. KuCoin API

### Get Single Ticker (Level 1 Order Book)
```bash
curl -X GET "https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=BTC-USDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Tickers
```bash
curl -X GET "https://api.kucoin.com/api/v1/market/allTickers" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get 24hr Stats
```bash
curl -X GET "https://api.kucoin.com/api/v1/market/stats?symbol=BTC-USDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## 6. OKX API

### Get Single Ticker
```bash
curl -X GET "https://www.okx.com/api/v5/market/ticker?instId=BTC-USDT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Spot Tickers
```bash
curl -X GET "https://www.okx.com/api/v5/market/tickers?instType=SPOT" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

### Get All Futures Tickers
```bash
curl -X GET "https://www.okx.com/api/v5/market/tickers?instType=FUTURES" \
  -H "Accept: application/json" \
  -H "User-Agent: Mozilla/5.0"
```

## Testing Your Application Endpoints

If you want to test your Spring Boot application endpoints, you can use these curl commands:

### Get Price from Specific Exchange
```bash
curl -X GET "http://localhost:8080/api/prices/BTC/USDT?exchange=KRAKEN" \
  -H "Accept: application/json"
```

### Get All Prices for a Symbol
```bash
curl -X GET "http://localhost:8080/api/prices/BTC/USDT" \
  -H "Accept: application/json"
```

### Get Price Comparison
```bash
curl -X GET "http://localhost:8080/api/price-comparison/BTC/USDT" \
  -H "Accept: application/json"
```

### Get Arbitrage Opportunities
```bash
curl -X GET "http://localhost:8080/api/arbitrage/opportunities" \
  -H "Accept: application/json"
```

## Common Trading Pairs for Testing

- **BTC/USDT**: Bitcoin to Tether
- **ETH/USDT**: Ethereum to Tether
- **BTC/USD**: Bitcoin to US Dollar
- **ETH/USD**: Ethereum to US Dollar
- **ADA/USDT**: Cardano to Tether
- **SOL/USDT**: Solana to Tether

## Notes

1. **Rate Limits**: Most exchanges have rate limits. Be mindful of how frequently you make requests.
2. **Symbol Formats**: Each exchange uses different symbol formats:
   - Kraken: `XBTUSDT` (Bitcoin is XBT)
   - Binance: `BTCUSDT`
   - Bybit: `BTCUSDT`
   - Coinbase: `BTC-USD`
   - KuCoin: `BTC-USDT`
   - OKX: `BTC-USDT`

3. **Error Handling**: All APIs return JSON responses with error information if something goes wrong.

4. **Authentication**: These examples use public endpoints that don't require authentication. Private endpoints would require API keys and signatures.

## Example Response Formats

### Kraken Response
```json
{
  "error": [],
  "result": {
    "XBTUSDT": {
      "a": ["50000.0", "1", "1.000"],
      "b": ["49999.0", "1", "1.000"],
      "c": ["50000.0", "0.001"],
      "v": ["1000.0", "2000.0"]
    }
  }
}
```

### Binance Response
```json
{
  "symbol": "BTCUSDT",
  "bidPrice": "49999.00000000",
  "bidQty": "1.00000000",
  "askPrice": "50000.00000000",
  "askQty": "1.00000000"
}
```

### Bybit Response
```json
{
  "retCode": 0,
  "retMsg": "OK",
  "result": {
    "category": "spot",
    "list": [
      {
        "symbol": "BTCUSDT",
        "lastPrice": "50000.00",
        "bid1Price": "49999.00",
        "ask1Price": "50000.00",
        "volume24h": "1000.00"
      }
    ]
  }
}
```
