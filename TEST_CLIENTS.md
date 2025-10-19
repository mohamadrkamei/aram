# تست سریع کلاینت‌های صرافی

## 🚀 راه‌اندازی

```bash
# اجرای برنامه
./mvnw spring-boot:run

# یا در ویندوز
mvnw.cmd spring-boot:run
```

منتظر بمانید تا ببینید:
```
Started AramApplication in X.XXX seconds
```

## ✅ تست‌های پایه

### 1. بررسی صرافی‌های فعال

```bash
curl http://localhost:8080/api/price-comparison/exchanges
```

**انتظار:** لیست 6 صرافی
```json
[
  {"name": "BINANCE", "displayName": "Binance", "healthy": true},
  {"name": "COINBASE", "displayName": "Coinbase", "healthy": true},
  ...
]
```

### 2. تست Binance

```bash
curl "http://localhost:8080/api/prices/BINANCE/BTC/USDT"
```

**انتظار:** قیمت Bitcoin از Binance

### 3. تست مقایسه - یک ارز

```bash
curl "http://localhost:8080/api/price-comparison/compare/BTC/USDT"
```

**انتظار:** مقایسه قیمت در 6 صرافی + محاسبه آربیتراژ

### 4. تست مقایسه - چند ارز

```bash
curl -X POST "http://localhost:8080/api/price-comparison/compare-multiple" \
  -H "Content-Type: application/json" \
  -d '["BTC/USDT", "ETH/USDT"]'
```

**انتظار:** مقایسه 2 ارز

## 🧪 تست هر صرافی جداگانه

### Binance
```bash
curl "http://localhost:8080/api/prices/BINANCE/BTC/USDT"
curl "http://localhost:8080/api/prices/BINANCE/ETH/USDT"
curl "http://localhost:8080/api/prices/BINANCE/SOL/USDT"
```

### Coinbase
```bash
curl "http://localhost:8080/api/prices/COINBASE/BTC/USDT"
curl "http://localhost:8080/api/prices/COINBASE/ETH/USDT"
```

### Kraken
```bash
curl "http://localhost:8080/api/prices/KRAKEN/BTC/USDT"
curl "http://localhost:8080/api/prices/KRAKEN/ETH/USDT"
```

### KuCoin
```bash
curl "http://localhost:8080/api/prices/KUCOIN/BTC/USDT"
curl "http://localhost:8080/api/prices/KUCOIN/ETH/USDT"
```

### Bybit
```bash
curl "http://localhost:8080/api/prices/BYBIT/BTC/USDT"
curl "http://localhost:8080/api/prices/BYBIT/ETH/USDT"
```

### OKX
```bash
curl "http://localhost:8080/api/prices/OKX/BTC/USDT"
curl "http://localhost:8080/api/prices/OKX/ETH/USDT"
```

## 📊 تست ارزهای مختلف

```bash
# Bitcoin
curl "http://localhost:8080/api/price-comparison/compare/BTC/USDT"

# Ethereum
curl "http://localhost:8080/api/price-comparison/compare/ETH/USDT"

# Solana
curl "http://localhost:8080/api/price-comparison/compare/SOL/USDT"

# BNB
curl "http://localhost:8080/api/price-comparison/compare/BNB/USDT"

# XRP
curl "http://localhost:8080/api/price-comparison/compare/XRP/USDT"

# Cardano
curl "http://localhost:8080/api/price-comparison/compare/ADA/USDT"
```

## 🔍 بررسی سلامت صرافی‌ها

```bash
curl "http://localhost:8080/api/exchanges/BINANCE/health"
curl "http://localhost:8080/api/exchanges/COINBASE/health"
curl "http://localhost:8080/api/exchanges/KRAKEN/health"
curl "http://localhost:8080/api/exchanges/KUCOIN/health"
curl "http://localhost:8080/api/exchanges/BYBIT/health"
curl "http://localhost:8080/api/exchanges/OKX/health"
```

## 🎯 تست آربیتراژ

### سناریو 1: یافتن بهترین فرصت
```bash
# مقایسه چند ارز پرطرفدار
curl -X POST "http://localhost:8080/api/price-comparison/compare-multiple" \
  -H "Content-Type: application/json" \
  -d '["BTC/USDT", "ETH/USDT", "SOL/USDT", "BNB/USDT", "XRP/USDT"]'
```

### سناریو 2: تشخیص خودکار
```bash
# فعال‌سازی scheduler برای تشخیص خودکار
# در application.properties:
# arbitrage.autoExecute=false  (فقط تشخیص، بدون اجرا)
```

## 🐛 عیب‌یابی

### خطا: Connection refused
```bash
# بررسی کنید برنامه در حال اجرا است
netstat -an | findstr :8080  # ویندوز
```

### خطا: 404 Not Found
```bash
# بررسی endpoint
curl http://localhost:8080/actuator/health
```

### خطا: Timeout
- شاید یک صرافی پاسخ نمی‌دهد (عادی است)
- سایر صرافی‌ها باید کار کنند

### خطا: Empty response
- ممکن است جفت ارز در آن صرافی وجود نداشته باشد
- ارز دیگری امتحان کنید

## 📈 نمایش نتایج زیبا

### با jq (اختیاری)
```bash
# نصب jq
# ویندوز: choco install jq
# Linux: sudo apt install jq

# استفاده
curl -s "http://localhost:8080/api/price-comparison/compare/BTC/USDT" | jq .
```

### با Python (اختیاری)
```bash
curl -s "http://localhost:8080/api/price-comparison/compare/BTC/USDT" | python -m json.tool
```

## 🎨 تست در مرورگر

1. مرورگر را باز کنید
2. آدرس را وارد کنید:

```
http://localhost:8080/api/price-comparison/compare/BTC/USDT
```

3. نتیجه را در مرورگر ببینید

برای نمایش بهتر، extension JSON Viewer نصب کنید:
- Chrome: JSON Viewer
- Firefox: JSONView

## 📝 Checklist تست

- [ ] برنامه بدون خطا اجرا شد
- [ ] `/api/price-comparison/exchanges` لیست 6 صرافی را برمی‌گرداند
- [ ] قیمت BTC از Binance دریافت می‌شود
- [ ] قیمت ETH از Coinbase دریافت می‌شود
- [ ] مقایسه BTC/USDT کار می‌کند
- [ ] مقایسه چند ارز همزمان کار می‌کند
- [ ] آربیتراژ محاسبه می‌شود
- [ ] Health check ها پاسخ می‌دهند

## 🚨 مشکلات متداول

### ⚠️ Rate Limiting
اگر خطای 429 دیدید:
- کمی صبر کنید (30-60 ثانیه)
- تعداد درخواست‌ها را کم کنید

### ⚠️ Symbol Not Found
برخی صرافی‌ها همه جفت ارزها را ندارند:
- Coinbase: جفت ارزهای کمتری دارد
- Kraken: فرمت‌های خاص دارد

### ⚠️ Network Error
- VPN فعال است؟ برخی کشورها دسترسی محدود دارند
- Firewall چیزی را بلاک نکرده؟
- اینترنت وصل است؟

## 🎯 نتیجه‌گیری

اگر همه تست‌ها موفق بودند:
✅ **کلاینت‌های صرافی به درستی کار می‌کنند!**

حالا می‌توانید:
1. صرافی‌های بیشتر اضافه کنید
2. WebSocket پیاده‌سازی کنید
3. Dashboard بسازید
4. استراتژی آربیتراژ بنویسید

## 📞 آماده برای مرحله بعد!

لیست صرافی‌های بعدی را بدهید یا بگویید چه قابلیتی می‌خواهید.


