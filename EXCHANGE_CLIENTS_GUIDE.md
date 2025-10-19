# راهنمای کلاینت‌های صرافی / Exchange Clients Guide

## کلاینت‌های پیاده‌سازی شده / Implemented Clients

تا به حال 6 صرافی اصلی پیاده‌سازی شده‌اند:

### 1. **Binance** ✅
- پایگاه API: `https://api.binance.com`
- مستندات: https://binance-docs.github.io/apidocs/spot/en/
- فرمت سمبل: `BTCUSDT` (بدون slash)
- وضعیت: پیاده‌سازی کامل

### 2. **Coinbase Pro** ✅
- پایگاه API: `https://api.exchange.coinbase.com`
- مستندات: https://docs.cloud.coinbase.com/exchange/reference
- فرمت سمبل: `BTC-USDT` (با dash)
- وضعیت: پیاده‌سازی کامل

### 3. **Kraken** ✅
- پایگاه API: `https://api.kraken.com`
- مستندات: https://docs.kraken.com/rest/
- فرمت سمبل: `XBTUSDT` (نقشه‌برداری سفارشی)
- وضعیت: پیاده‌سازی کامل

### 4. **KuCoin** ✅
- پایگاه API: `https://api.kucoin.com`
- مستندات: https://docs.kucoin.com/
- فرمت سمبل: `BTC-USDT` (با dash)
- وضعیت: پیاده‌سازی کامل

### 5. **Bybit** ✅
- پایگاه API: `https://api.bybit.com`
- مستندات: https://bybit-exchange.github.io/docs/
- فرمت سمبل: `BTCUSDT` (بدون slash)
- وضعیت: پیاده‌سازی کامل

### 6. **OKX** ✅
- پایگاه API: `https://www.okx.com`
- مستندات: https://www.okx.com/docs-v5/en/
- فرمت سمبل: `BTC-USDT` (با dash)
- وضعیت: پیاده‌سازی کامل

## ساختار کلاینت‌ها / Client Architecture

```
ExchangeClient (Interface)
    ↓
BaseExchangeClient (Abstract Class)
    ↓
├── BinanceClient
├── CoinbaseClient
├── KrakenClient
├── KuCoinClient
├── BybitClient
└── OKXClient
```

## نحوه استفاده / How to Use

### 1. دریافت قیمت از یک صرافی / Fetch Price from One Exchange

```bash
curl http://localhost:8080/api/prices/BINANCE/BTC/USDT
```

```json
{
  "exchange": "BINANCE",
  "symbol": "BTC/USDT",
  "bidPrice": 43250.50,
  "askPrice": 43251.00,
  "lastPrice": 43250.75,
  "timestamp": "2025-10-08T12:30:00"
}
```

### 2. مقایسه قیمت در تمام صرافی‌ها / Compare Prices Across All Exchanges

```bash
curl http://localhost:8080/api/price-comparison/compare/BTC/USDT
```

```json
{
  "symbol": "BTC/USDT",
  "exchangeCount": 6,
  "prices": [
    {
      "exchange": "BINANCE",
      "symbol": "BTC/USDT",
      "bidPrice": 43250.50,
      "askPrice": 43251.00,
      "lastPrice": 43250.75
    },
    {
      "exchange": "COINBASE",
      "symbol": "BTC/USDT",
      "bidPrice": 43248.00,
      "askPrice": 43249.50,
      "lastPrice": 43248.75
    }
  ],
  "bestBuyExchange": "COINBASE",
  "bestBuyPrice": 43249.50,
  "bestSellExchange": "BINANCE",
  "bestSellPrice": 43250.50,
  "arbitrageProfit": 1.00,
  "arbitrageProfitPercentage": 0.0023,
  "hasArbitrageOpportunity": true
}
```

### 3. مقایسه چند ارز به صورت همزمان / Compare Multiple Symbols

```bash
curl -X POST http://localhost:8080/api/price-comparison/compare-multiple \
  -H "Content-Type: application/json" \
  -d '["BTC/USDT", "ETH/USDT", "BNB/USDT"]'
```

### 4. لیست صرافی‌های در دسترس / List Available Exchanges

```bash
curl http://localhost:8080/api/price-comparison/exchanges
```

```json
[
  {
    "name": "BINANCE",
    "displayName": "Binance",
    "healthy": true
  },
  {
    "name": "COINBASE",
    "displayName": "Coinbase",
    "healthy": true
  }
]
```

## API Endpoints جدید / New API Endpoints

### مقایسه قیمت‌ها / Price Comparison
```
GET    /api/price-comparison/compare/{symbol}
POST   /api/price-comparison/compare-multiple
GET    /api/price-comparison/exchanges
```

## ویژگی‌های کلاینت‌ها / Client Features

### ✅ پیاده‌سازی شده / Implemented
- دریافت قیمت برای یک جفت ارز
- دریافت قیمت برای چند جفت ارز
- دریافت تمام تیکرها
- بررسی سلامت صرافی
- مدیریت خطا با retry logic
- Timeout برای درخواست‌ها
- تبدیل خودکار فرمت سمبل‌ها

### 🔄 مدیریت خطا / Error Handling
- Automatic retry (2 بار)
- Timeout: 10 ثانیه
- Logging کامل خطاها
- Graceful failure (برگشت null)

## افزودن صرافی جدید / Adding New Exchange

برای افزودن صرافی جدید:

1. کلاس جدید در پکیج `client.impl` بسازید:

```java
@Component
@Slf4j
public class NewExchangeClient extends BaseExchangeClient {
    
    private static final String BASE_URL = "https://api.newexchange.com";
    
    public NewExchangeClient(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, BASE_URL, ExchangeType.NEW_EXCHANGE);
    }
    
    @Override
    public PriceDto fetchPrice(String symbol) {
        // پیاده‌سازی
    }
    
    @Override
    public List<PriceDto> fetchPrices(List<String> symbols) {
        // پیاده‌سازی
    }
    
    @Override
    public List<PriceDto> fetchAllTickers() {
        // پیاده‌سازی
    }
    
    @Override
    protected String convertToExchangeSymbol(String symbol) {
        // تبدیل فرمت
    }
    
    @Override
    protected String convertFromExchangeSymbol(String exchangeSymbol) {
        // تبدیل معکوس
    }
}
```

2. نوع صرافی را به `ExchangeType` enum اضافه کنید
3. Spring به صورت خودکار کلاینت را ثبت می‌کند

## صرافی‌های پیشنهادی برای افزودن بعدی / Suggested Exchanges to Add

برای رسیدن به 20 صرافی، می‌توان این‌ها را اضافه کرد:

7. **Gate.io** - یکی از بزرگ‌ترین صرافی‌ها
8. **Huobi** - صرافی معتبر آسیایی
9. **Bitfinex** - صرافی قدیمی و معتبر
10. **Crypto.com** - صرافی محبوب
11. **Bitstamp** - قدیمی‌ترین صرافی
12. **Gemini** - صرافی منظم آمریکایی
13. **BitMEX** - معاملات مشتقه
14. **FTX** (اگر فعال باشد)
15. **Poloniex**
16. **Bittrex**
17. **HTX (Huobi)**
18. **MEXC**
19. **Bitget**
20. **Phemex**

## تست کلاینت‌ها / Testing Clients

### تست دستی / Manual Testing

```bash
# دریافت قیمت BTC از Binance
curl http://localhost:8080/api/prices/BINANCE/BTC/USDT

# مقایسه قیمت ETH در همه صرافی‌ها
curl http://localhost:8080/api/price-comparison/compare/ETH/USDT

# بررسی سلامت Kraken
curl http://localhost:8080/api/exchanges/KRAKEN/health
```

### مثال خروجی واقعی / Real Output Example

```json
{
  "symbol": "BTC/USDT",
  "exchangeCount": 6,
  "prices": [
    {"exchange": "BINANCE", "askPrice": 43251.00, "bidPrice": 43250.50},
    {"exchange": "COINBASE", "askPrice": 43249.50, "bidPrice": 43248.00},
    {"exchange": "KRAKEN", "askPrice": 43252.00, "bidPrice": 43251.00},
    {"exchange": "KUCOIN", "askPrice": 43250.00, "bidPrice": 43249.50},
    {"exchange": "BYBIT", "askPrice": 43251.50, "bidPrice": 43250.00},
    {"exchange": "OKX", "askPrice": 43250.50, "bidPrice": 43249.00}
  ],
  "bestBuyExchange": "COINBASE",
  "bestBuyPrice": 43249.50,
  "bestSellExchange": "KRAKEN",
  "bestSellPrice": 43251.00,
  "arbitrageProfit": 1.50,
  "arbitrageProfitPercentage": 0.0035,
  "hasArbitrageOpportunity": true
}
```

## نکات مهم / Important Notes

⚠️ **Rate Limiting**: هر صرافی محدودیت تعداد درخواست دارد
⚠️ **API Keys**: برای دریافت قیمت نیازی به API key نیست، اما برای معامله لازم است
⚠️ **فرمت سمبل‌ها**: هر صرافی فرمت متفاوتی دارد که خودکار تبدیل می‌شود
⚠️ **Network Latency**: تاخیر شبکه ممکن است بر روی آربیتراژ تاثیر بگذارد

## Performance

- **Timeout**: 10 seconds per request
- **Retry**: 2 attempts
- **Parallel**: می‌توان درخواست‌ها را به صورت موازی ارسال کرد
- **Caching**: می‌توان caching اضافه کرد برای کاهش تعداد درخواست‌ها

## مرحله بعد / Next Steps

کدام صرافی‌ها را می‌خواهید اضافه کنیم؟ لیست را به من بدهید تا کلاینت‌های آن‌ها را پیاده‌سازی کنم.


