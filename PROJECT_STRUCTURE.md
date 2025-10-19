# Project Structure

```
aram/
│
├── pom.xml                                 # Maven configuration with all dependencies
├── README.md                               # Project documentation
├── ARCHITECTURE.md                         # System architecture details
├── QUICK_START.md                         # Quick start guide
├── PROJECT_STRUCTURE.md                   # This file
│
├── src/
│   ├── main/
│   │   ├── java/com/example/aram/
│   │   │   │
│   │   │   ├── AramApplication.java       # Spring Boot main application
│   │   │   │
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   ├── ArbitrageConfig.java   # Arbitrage settings (min profit, pairs, etc.)
│   │   │   │   ├── SchedulerConfig.java   # Enable scheduled tasks
│   │   │   │   └── WebClientConfig.java   # HTTP client configuration
│   │   │   │
│   │   │   ├── controller/                # REST API Controllers
│   │   │   │   ├── ArbitrageController.java   # /api/arbitrage/* endpoints
│   │   │   │   ├── ExchangeController.java    # /api/exchanges/* endpoints
│   │   │   │   └── PriceController.java       # /api/prices/* endpoints
│   │   │   │
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── ArbitrageOpportunityDto.java
│   │   │   │   ├── ExchangeConfigDto.java
│   │   │   │   └── PriceDto.java
│   │   │   │
│   │   │   ├── enums/                     # Enumerations
│   │   │   │   ├── ArbitrageType.java     # SIMPLE, TRIANGULAR, CROSS_EXCHANGE
│   │   │   │   ├── ExchangeType.java      # BINANCE, COINBASE, KRAKEN, etc.
│   │   │   │   ├── OpportunityStatus.java # DETECTED, EXECUTING, COMPLETED, etc.
│   │   │   │   ├── OrderSide.java         # BUY, SELL
│   │   │   │   └── OrderType.java         # MARKET, LIMIT, STOP_LOSS, etc.
│   │   │   │
│   │   │   ├── exception/                 # Exception handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── model/                     # JPA Entities (Database tables)
│   │   │   │   ├── ArbitrageOpportunity.java  # Detected opportunities
│   │   │   │   ├── Exchange.java              # Exchange configurations
│   │   │   │   ├── Price.java                 # Price data
│   │   │   │   ├── Trade.java                 # Executed trades
│   │   │   │   └── TradingPair.java           # Trading pairs (BTC/USDT, etc.)
│   │   │   │
│   │   │   ├── repository/                # Data access layer (Spring Data JPA)
│   │   │   │   ├── ArbitrageOpportunityRepository.java
│   │   │   │   ├── ExchangeRepository.java
│   │   │   │   ├── PriceRepository.java
│   │   │   │   ├── TradeRepository.java
│   │   │   │   └── TradingPairRepository.java
│   │   │   │
│   │   │   ├── scheduler/                 # Scheduled tasks
│   │   │   │   ├── ArbitrageDetectionScheduler.java  # Detect opportunities
│   │   │   │   └── PriceUpdateScheduler.java         # Update prices
│   │   │   │
│   │   │   ├── service/                   # Business logic
│   │   │   │   ├── ArbitrageDetectionService.java    # Opportunity detection logic
│   │   │   │   ├── ExchangeService.java              # Exchange API interactions
│   │   │   │   ├── PriceService.java                 # Price management
│   │   │   │   └── TradeExecutionService.java        # Trade execution logic
│   │   │   │
│   │   │   └── util/                      # Utility classes
│   │   │       └── ArbitrageCalculator.java  # Profit calculations
│   │   │
│   │   └── resources/
│   │       ├── application.properties     # Application configuration
│   │       ├── static/                    # Static resources (empty for now)
│   │       └── templates/                 # Templates (empty for now)
│   │
│   └── test/
│       └── java/com/example/aram/
│           └── AramApplicationTests.java  # Test class
│
└── target/                                # Compiled classes (auto-generated)
```

## Package Responsibilities

### 📦 **config/**
Configuration classes for the application
- `ArbitrageConfig`: Application-specific settings loaded from properties
- `SchedulerConfig`: Enables Spring's scheduled task execution
- `WebClientConfig`: Configures reactive HTTP client for external APIs

### 📦 **controller/**
REST API endpoints - handles HTTP requests and responses
- `ArbitrageController`: Manages arbitrage operations (detect, list, execute)
- `ExchangeController`: Manages exchange configurations
- `PriceController`: Provides price data access

### 📦 **dto/**
Data Transfer Objects - used for API requests/responses
- Lightweight objects without business logic
- Clean separation between API and internal models

### 📦 **enums/**
Enumeration types for type-safe constants
- Reduces magic strings
- Provides compile-time safety

### 📦 **exception/**
Centralized exception handling
- `GlobalExceptionHandler`: Catches exceptions and returns proper HTTP responses

### 📦 **model/**
JPA entities representing database tables
- Each class maps to a database table
- Contains business data and relationships
- Includes JPA annotations (@Entity, @Table, @Column, etc.)

### 📦 **repository/**
Data access layer using Spring Data JPA
- Provides CRUD operations
- Custom queries for complex data retrieval
- Automatic implementation by Spring

### 📦 **scheduler/**
Background tasks that run on a schedule
- `PriceUpdateScheduler`: Fetches prices from exchanges periodically
- `ArbitrageDetectionScheduler`: Scans for arbitrage opportunities

### 📦 **service/**
Business logic layer
- Core application functionality
- Transaction management
- Orchestrates multiple operations

### 📦 **util/**
Helper classes and utilities
- Reusable calculation functions
- Common utilities

## Key Files

### 🔑 `pom.xml`
Maven project configuration:
- Spring Boot 3.5.6
- Spring Data JPA (database access)
- Spring WebFlux (reactive HTTP client)
- H2 Database (in-memory)
- Lombok (reduces boilerplate)
- Validation API
- WebSocket support

### 🔑 `application.properties`
Runtime configuration:
- Database connection
- Server port
- Arbitrage settings (min profit, watched pairs)
- Logging levels
- JPA/Hibernate settings

### 🔑 `AramApplication.java`
Application entry point:
- `@SpringBootApplication` - enables auto-configuration
- `@EnableConfigurationProperties` - enables configuration classes
- `main()` method - starts the application

## Database Tables (Auto-created by JPA)

```
trading_pairs
├── id (PK)
├── symbol (e.g., "BTC/USDT")
├── base_currency (e.g., "BTC")
├── quote_currency (e.g., "USDT")
├── active
├── created_at
└── updated_at

exchanges
├── id (PK)
├── exchange_type (ENUM)
├── api_key
├── api_secret
├── enabled
├── fee_percentage
├── withdrawal_fee
├── last_health_check
├── is_healthy
├── created_at
└── updated_at

prices
├── id (PK)
├── exchange_type (ENUM)
├── symbol
├── bid_price
├── ask_price
├── last_price
├── volume_24h
├── timestamp
└── created_at

arbitrage_opportunities
├── id (PK)
├── arbitrage_type (ENUM)
├── symbol
├── buy_exchange (ENUM)
├── sell_exchange (ENUM)
├── buy_price
├── sell_price
├── profit_percentage
├── estimated_profit
├── volume
├── status (ENUM)
├── execution_details
├── detected_at
├── executed_at
├── completed_at
├── created_at
└── updated_at

trades
├── id (PK)
├── opportunity_id (FK)
├── exchange_type (ENUM)
├── symbol
├── order_type (ENUM)
├── order_side (ENUM)
├── price
├── quantity
├── executed_quantity
├── fee
├── external_order_id
├── status
├── created_at
└── executed_at
```

## Flow of Data

### 1. Price Updates
```
Scheduler → PriceService → ExchangeService → External APIs
                                             ↓
                                      PriceRepository → Database
```

### 2. Arbitrage Detection
```
Scheduler → ArbitrageDetectionService → PriceService → Database
                 ↓
           ArbitrageCalculator (profit calculations)
                 ↓
        ArbitrageOpportunityRepository → Database
```

### 3. Trade Execution
```
Controller/Scheduler → TradeExecutionService → ExchangeService → External APIs
                              ↓
                       TradeRepository → Database
                              ↓
                 Update ArbitrageOpportunity status
```

## Component Count Summary

- **Enums**: 5 files
- **Models**: 5 entities
- **DTOs**: 3 files
- **Repositories**: 5 interfaces
- **Services**: 4 classes
- **Controllers**: 3 classes
- **Schedulers**: 2 classes
- **Config**: 3 classes
- **Utils**: 1 class
- **Exception Handlers**: 1 class

**Total**: 32 Java files + configuration files

## Next Development Steps

1. ✅ Project structure created
2. ⏳ Implement exchange API integrations
3. ⏳ Add WebSocket for real-time prices
4. ⏳ Implement authentication/security
5. ⏳ Add unit and integration tests
6. ⏳ Implement risk management features
7. ⏳ Add monitoring and alerting
8. ⏳ Production database setup
9. ⏳ Deploy to production

