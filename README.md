# Arbitrage Trading System

A comprehensive Spring Boot application for detecting and executing cryptocurrency arbitrage opportunities across multiple exchanges.

## Features

- **Multi-Exchange Support**: Monitor prices across Binance, Coinbase, Kraken, KuCoin, Bybit, and OKX
- **Real-time Price Tracking**: Continuous price updates with configurable intervals
- **Arbitrage Detection**: Automatic detection of simple arbitrage opportunities
- **Trade Execution**: Manual and automatic trade execution capabilities
- **Historical Data**: Price history storage and analysis
- **RESTful API**: Complete REST API for all operations
- **Scheduler**: Automated price updates and opportunity scanning

## Project Structure

```
src/main/java/com/example/aram/
├── config/              # Configuration classes
│   ├── ArbitrageConfig.java
│   ├── SchedulerConfig.java
│   └── WebClientConfig.java
├── controller/          # REST controllers
│   ├── ArbitrageController.java
│   ├── ExchangeController.java
│   └── PriceController.java
├── dto/                 # Data Transfer Objects
│   ├── ArbitrageOpportunityDto.java
│   ├── ExchangeConfigDto.java
│   └── PriceDto.java
├── enums/              # Enumerations
│   ├── ArbitrageType.java
│   ├── ExchangeType.java
│   ├── OpportunityStatus.java
│   ├── OrderSide.java
│   └── OrderType.java
├── exception/          # Exception handlers
│   └── GlobalExceptionHandler.java
├── model/              # JPA entities
│   ├── ArbitrageOpportunity.java
│   ├── Exchange.java
│   ├── Price.java
│   ├── Trade.java
│   └── TradingPair.java
├── repository/         # Data repositories
│   ├── ArbitrageOpportunityRepository.java
│   ├── ExchangeRepository.java
│   ├── PriceRepository.java
│   ├── TradeRepository.java
│   └── TradingPairRepository.java
├── scheduler/          # Scheduled tasks
│   ├── ArbitrageDetectionScheduler.java
│   └── PriceUpdateScheduler.java
├── service/            # Business logic
│   ├── ArbitrageDetectionService.java
│   ├── ExchangeService.java
│   ├── PriceService.java
│   └── TradeExecutionService.java
├── util/               # Utility classes
│   └── ArbitrageCalculator.java
└── AramApplication.java
```

## Technologies

- **Java 25**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring WebFlux** (for reactive HTTP calls)
- **H2 Database** (in-memory for development)
- **Lombok**
- **Maven**

## API Endpoints

### Arbitrage Operations
- `POST /api/arbitrage/detect` - Detect arbitrage opportunities
- `GET /api/arbitrage/opportunities` - Get all profitable opportunities
- `GET /api/arbitrage/opportunities/{id}` - Get specific opportunity
- `POST /api/arbitrage/execute/{opportunityId}` - Execute an opportunity

### Price Operations
- `GET /api/prices/{exchange}/{symbol}` - Get price for symbol on exchange
- `GET /api/prices/{symbol}` - Get prices for symbol across all exchanges
- `POST /api/prices/update` - Manually trigger price update

### Exchange Operations
- `GET /api/exchanges` - Get all active exchanges
- `GET /api/exchanges/{exchangeType}` - Get specific exchange
- `GET /api/exchanges/{exchangeType}/health` - Check exchange health

## Configuration

Edit `application.properties` to configure:

```properties
# Minimum profit percentage to detect opportunities
arbitrage.minProfitPercentage=0.5

# Maximum balance percentage to use per trade
arbitrage.maxBalancePercentage=0.1

# Trading pairs to monitor
arbitrage.watchedPairs=BTC/USDT,ETH/USDT,BNB/USDT

# Price update interval (milliseconds)
arbitrage.priceUpdateInterval=5000

# Enable automatic trade execution (be careful!)
arbitrage.autoExecute=false

# Days to keep price history
arbitrage.priceHistoryDays=7
```

## Getting Started

### Prerequisites
- Java 25 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run with Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Accessing H2 Console

Visit `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:arbitrage`
- Username: `sa`
- Password: (leave empty)

## Next Steps

1. **Implement Exchange Integrations**: The `ExchangeService` contains placeholders for actual exchange API calls
2. **Add WebSocket Support**: For real-time price streaming
3. **Implement Authentication**: Add security for API endpoints
4. **Add Risk Management**: Implement position limits and risk controls
5. **Deploy to Production Database**: Replace H2 with PostgreSQL or MySQL
6. **Add Monitoring**: Integrate with Prometheus/Grafana
7. **Add Tests**: Comprehensive unit and integration tests

## Important Notes

⚠️ **Warning**: This is a development framework. Before using with real funds:
- Implement proper exchange API integrations
- Add comprehensive error handling
- Implement risk management
- Add proper authentication and authorization
- Test thoroughly with small amounts first
- Understand the risks of automated trading

## License

This project is for educational purposes.

