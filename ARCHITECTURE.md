# Arbitrage System Architecture

## System Overview

This arbitrage trading system is designed to detect and execute cryptocurrency arbitrage opportunities across multiple exchanges.

## Architecture Layers

### 1. **Presentation Layer** (Controllers)
```
ArbitrageController    → Arbitrage opportunity management
PriceController        → Price data access
ExchangeController     → Exchange configuration
```

### 2. **Business Logic Layer** (Services)
```
ArbitrageDetectionService  → Detects arbitrage opportunities
TradeExecutionService      → Executes trades on exchanges
PriceService              → Manages price data
ExchangeService           → Interacts with exchange APIs
```

### 3. **Data Layer** (Repositories + Entities)
```
Entities:
- TradingPair           → Trading pair information (BTC/USDT, ETH/USDT)
- Exchange              → Exchange configuration and credentials
- Price                 → Real-time and historical price data
- ArbitrageOpportunity  → Detected arbitrage opportunities
- Trade                 → Executed trades

Repositories:
- TradingPairRepository
- ExchangeRepository
- PriceRepository
- ArbitrageOpportunityRepository
- TradeRepository
```

## Data Flow

### Price Update Flow
```
┌─────────────────────┐
│ PriceUpdateScheduler│ (Every 5 seconds)
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│   PriceService      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  ExchangeService    │ (Fetch from exchanges)
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  PriceRepository    │ (Store in DB)
└─────────────────────┘
```

### Arbitrage Detection Flow
```
┌────────────────────────────┐
│ ArbitrageDetectionScheduler│ (Every 5 seconds)
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│ ArbitrageDetectionService  │
└──────────┬─────────────────┘
           │
           ▼ (Get latest prices)
┌────────────────────────────┐
│      PriceService          │
└──────────┬─────────────────┘
           │
           ▼ (Compare prices)
┌────────────────────────────┐
│   ArbitrageCalculator      │
└──────────┬─────────────────┘
           │
           ▼ (If profitable)
┌────────────────────────────┐
│ ArbitrageOpportunityRepo   │ (Save opportunity)
└────────────────────────────┘
```

### Trade Execution Flow
```
┌─────────────────────┐
│ ArbitrageController │ (Manual trigger)
│        OR           │
│ Detection Scheduler │ (Auto trigger)
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│TradeExecutionService│
└──────────┬──────────┘
           │
           ├──────────────────┐
           │                  │
           ▼                  ▼
  ┌────────────────┐  ┌────────────────┐
  │Buy on Exchange1│  │Sell on Exchange2│
  └────────┬───────┘  └────────┬───────┘
           │                  │
           ▼                  ▼
  ┌────────────────┐  ┌────────────────┐
  │  TradeRepo     │  │  TradeRepo     │
  │ (Save trade)   │  │ (Save trade)   │
  └────────────────┘  └────────────────┘
```

## Key Components

### 1. Enums
- `ExchangeType`: Supported exchanges (BINANCE, COINBASE, KRAKEN, etc.)
- `OrderType`: Market, Limit, Stop-Loss, Stop-Limit
- `OrderSide`: BUY, SELL
- `ArbitrageType`: SIMPLE, TRIANGULAR, CROSS_EXCHANGE
- `OpportunityStatus`: DETECTED, ANALYZING, EXECUTING, COMPLETED, FAILED, EXPIRED

### 2. DTOs (Data Transfer Objects)
- `PriceDto`: Price information for API responses
- `ArbitrageOpportunityDto`: Opportunity information for API responses
- `ExchangeConfigDto`: Exchange configuration

### 3. Configuration
- `ArbitrageConfig`: Application settings (min profit, watched pairs, etc.)
- `SchedulerConfig`: Enable scheduled tasks
- `WebClientConfig`: HTTP client configuration

### 4. Utilities
- `ArbitrageCalculator`: Profit calculations and trade amount optimization

### 5. Exception Handling
- `GlobalExceptionHandler`: Centralized exception handling for REST APIs

## API Endpoints

### Arbitrage Endpoints
```
POST   /api/arbitrage/detect                    → Detect opportunities
GET    /api/arbitrage/opportunities             → List opportunities
GET    /api/arbitrage/opportunities/{id}        → Get specific opportunity
POST   /api/arbitrage/execute/{opportunityId}   → Execute arbitrage
```

### Price Endpoints
```
GET    /api/prices/{exchange}/{symbol}   → Get price on exchange
GET    /api/prices/{symbol}              → Get prices on all exchanges
POST   /api/prices/update                → Manual price update
```

### Exchange Endpoints
```
GET    /api/exchanges                    → List active exchanges
GET    /api/exchanges/{exchangeType}     → Get exchange details
GET    /api/exchanges/{exchangeType}/health → Check exchange health
```

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `arbitrage.minProfitPercentage` | 0.5 | Minimum profit % to detect |
| `arbitrage.maxBalancePercentage` | 0.1 | Max balance % per trade |
| `arbitrage.watchedPairs` | BTC/USDT, ETH/USDT, ... | Pairs to monitor |
| `arbitrage.priceUpdateInterval` | 5000 | Price update interval (ms) |
| `arbitrage.autoExecute` | false | Enable auto-execution |
| `arbitrage.priceHistoryDays` | 7 | Days to keep price history |

## Database Schema

### Trading Pairs
- Stores supported trading pairs
- Active/inactive status

### Exchanges
- Exchange credentials and configuration
- Fee information
- Health status

### Prices
- Real-time and historical price data
- Bid, ask, last price, and volume
- Indexed by exchange, symbol, and timestamp

### Arbitrage Opportunities
- Detected opportunities
- Buy/sell exchange and prices
- Profit calculations
- Execution status

### Trades
- Executed trade records
- Links to opportunities
- Order details and status

## Workflow Example

1. **Price Update** (Every 5 seconds):
   - Scheduler triggers price update
   - Fetch prices from all active exchanges
   - Store in database

2. **Opportunity Detection** (Every 5 seconds):
   - Get latest prices for all watched pairs
   - Compare prices across exchanges
   - Calculate profit percentage
   - Save opportunities above threshold

3. **Manual Execution**:
   - User calls `/api/arbitrage/execute/{id}`
   - System validates opportunity
   - Executes buy order on cheaper exchange
   - Executes sell order on expensive exchange
   - Records trades in database

4. **Auto Execution** (If enabled):
   - Scheduler detects new opportunities
   - Automatically executes profitable trades
   - Records results

## Security Considerations

⚠️ **Important**: Before production use:
1. Add authentication/authorization
2. Encrypt API keys in database
3. Implement rate limiting
4. Add request validation
5. Secure sensitive endpoints
6. Implement audit logging

## Next Steps for Implementation

1. **Exchange Integration**: Implement actual API calls in `ExchangeService`
2. **WebSocket Support**: Add real-time price streaming
3. **Risk Management**: Position limits, stop-loss mechanisms
4. **Backtesting**: Historical data analysis
5. **Monitoring**: Prometheus metrics, alerts
6. **Testing**: Unit and integration tests
7. **Production Database**: Switch from H2 to PostgreSQL/MySQL

