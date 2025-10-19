# Quick Start Guide

## Installation & Setup

### 1. Prerequisites
- Java 25 (or Java 17+, update `java.version` in pom.xml)
- Maven 3.6+
- Git

### 2. Run the Application

```bash
# Navigate to project directory
cd aram

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Testing the API

### 1. Check Application Health

```bash
curl http://localhost:8080/api/exchanges
```

### 2. Update Prices (Manual Trigger)

```bash
curl -X POST http://localhost:8080/api/prices/update \
  -H "Content-Type: application/json" \
  -d '["BTC/USDT", "ETH/USDT"]'
```

### 3. Detect Arbitrage Opportunities

```bash
curl -X POST "http://localhost:8080/api/arbitrage/detect?symbol=BTC/USDT&minProfitPercentage=0.5"
```

### 4. Get All Opportunities

```bash
curl "http://localhost:8080/api/arbitrage/opportunities?minProfit=0.5"
```

### 5. Get Prices for a Symbol

```bash
curl http://localhost:8080/api/prices/BTC/USDT
```

### 6. Execute Arbitrage (Manual)

```bash
curl -X POST "http://localhost:8080/api/arbitrage/execute/1?amount=0.01"
```

## Accessing H2 Database Console

1. Open browser: `http://localhost:8080/h2-console`
2. Use these credentials:
   - **JDBC URL**: `jdbc:h2:mem:arbitrage`
   - **Username**: `sa`
   - **Password**: (leave empty)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Minimum profit percentage to detect opportunities
arbitrage.minProfitPercentage=0.5

# Trading pairs to monitor
arbitrage.watchedPairs=BTC/USDT,ETH/USDT,BNB/USDT,SOL/USDT,XRP/USDT

# Price update interval (milliseconds) - 5 seconds
arbitrage.priceUpdateInterval=5000

# Enable automatic trade execution (CAUTION!)
arbitrage.autoExecute=false
```

## Scheduled Tasks

The application runs two scheduled tasks:

1. **Price Update Scheduler** (every 5 seconds):
   - Fetches prices from all active exchanges
   - Updates database with latest prices

2. **Arbitrage Detection Scheduler** (every 5 seconds):
   - Analyzes prices for arbitrage opportunities
   - Saves profitable opportunities to database
   - Auto-executes if `arbitrage.autoExecute=true`

## Example Workflow

### Step 1: Add Exchange Configuration
First, you need to add exchange configurations to the database. You can do this manually via H2 console:

```sql
INSERT INTO exchanges (exchange_type, api_key, api_secret, enabled, fee_percentage, withdrawal_fee, is_healthy, created_at, updated_at) 
VALUES 
  ('BINANCE', 'your_api_key', 'your_api_secret', true, 0.1, 0.0005, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('COINBASE', 'your_api_key', 'your_api_secret', true, 0.2, 0.001, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### Step 2: Add Trading Pairs
Add the pairs you want to monitor:

```sql
INSERT INTO trading_pairs (symbol, base_currency, quote_currency, active, created_at, updated_at) 
VALUES 
  ('BTC/USDT', 'BTC', 'USDT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('ETH/USDT', 'ETH', 'USDT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### Step 3: Start Monitoring
Once configured, the schedulers will automatically:
1. Fetch prices every 5 seconds
2. Detect arbitrage opportunities
3. Store them in the database

### Step 4: View Opportunities
```bash
# Get all opportunities with min 0.5% profit
curl "http://localhost:8080/api/arbitrage/opportunities?minProfit=0.5"
```

### Step 5: Execute Arbitrage
```bash
# Execute opportunity with ID 1, trading 0.01 BTC
curl -X POST "http://localhost:8080/api/arbitrage/execute/1?amount=0.01"
```

## Monitoring Logs

View application logs to see activity:

```bash
# Watch price updates
tail -f logs/spring.log | grep "Updating prices"

# Watch arbitrage detection
tail -f logs/spring.log | grep "Scanning for arbitrage"
```

## Important Notes

### ⚠️ Before Real Trading

1. **Implement Exchange APIs**: The `ExchangeService` has placeholder methods. You need to implement actual API calls to exchanges.

2. **Test with Small Amounts**: Always start with very small amounts.

3. **Add Security**: 
   - Implement authentication
   - Encrypt API keys
   - Add rate limiting

4. **Risk Management**:
   - Set position limits
   - Implement stop-loss
   - Monitor account balances

5. **Network Latency**: Arbitrage opportunities can disappear quickly. Consider:
   - Using WebSocket for real-time prices
   - Hosting near exchange servers
   - Optimizing execution speed

6. **Fees**: Always account for:
   - Trading fees
   - Withdrawal fees
   - Network fees (for crypto transfers)
   - Slippage

## Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Database Connection Issues
```bash
# Check H2 console is enabled
spring.h2.console.enabled=true
```

### Schedulers Not Running
```bash
# Verify scheduling is enabled in logs
# Look for: "Updating prices" and "Scanning for arbitrage"
```

## Development Mode

For development, you might want to:

1. **Increase logging**:
```properties
logging.level.com.example.aram=DEBUG
```

2. **Disable auto-execution**:
```properties
arbitrage.autoExecute=false
```

3. **Reduce update interval** (for testing):
```properties
arbitrage.priceUpdateInterval=10000  # 10 seconds
```

## Production Deployment

For production:

1. **Switch to PostgreSQL/MySQL**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/arbitrage
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

2. **Disable H2 Console**:
```properties
spring.h2.console.enabled=false
```

3. **Configure proper logging**:
```properties
logging.file.name=logs/arbitrage.log
logging.level.com.example.aram=INFO
```

4. **Use environment variables** for sensitive data:
```bash
export DB_PASSWORD=your_password
export EXCHANGE_API_KEY=your_key
```

## Support

For issues or questions:
1. Check the logs in H2 console or application logs
2. Review the API documentation in README.md
3. Check the architecture documentation in ARCHITECTURE.md

