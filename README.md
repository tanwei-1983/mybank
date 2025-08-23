# Bank Transaction Management System

A bank transaction management system based on Spring Boot and Mybatis, which provides complete transaction CRUD operations, paginated queries, caching mechanisms, and support for containerized deployment.

## Tech Stack
- **Java 17**
- **Spring Boot 2.7.7**
- **MyBatis 2.1.4**
- **H2 memory database**
- **Caffeine cache**
- **Maven**
- **Docker & Kubernetes**

### Requirements
- openjdk:17-jdk-slim
- Maven 3.6+
- Docker 
- Kubernetes

### Docker running

1. **Build the docker image**
```bash
docker build -t app_tom:1.0 .
```

2. **Run the container**
```bash
docker run --rm -p 8080:8080 app_tom:1.0
```

### Kubernetes deployment

1. **build the docker image**
```bash
docker build -t app_tom:1.0 .
```

2. **deploy to Kubernetes**
```bash
kubectl apply -f k8s/deployment.yaml
```

## API document
#### 1. create transacion
```bash
curl -X POST 'http://localhost:8080/api/v1/mybank/transactions' -H "Content-Type: application/json" \
-d '{"accountNumber": "6777", "transactionType":"WITHDRAW", "amount":6777, "currency":"USD","description":"new description","category":"new catetory"}'
```

#### 2. update the transaction (you can get the id from previous 'create transacion')
```bash
curl -X PUT 'http://localhost:8080/api/v1/mybank/transactions/{id}'  -H "Content-Type: application/json" \
-d '{"accountNumber": "223332", "transactionType":"WITHDRAW", "amount":1777, "currency":"USD","description":"update description","category":"update category", "status":"NORMAL"}'
```

#### 3. Pagination query for all transaction requests
```bash
curl -X GET 'http://localhost:8080/api/v1/mybank/transactions?page=1&size=20'
```

#### 4. delete the transaction
```bash
curl -X DELETE 'http://localhost:8080/api/v1/mybank/transactions/{id}'
```
### API response data format
```json
{
  "success": true,
  "message": "success",
  "data": {
     
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

## project structure
src/
├── main/
│   └── java/
│       └── com/
│           └── mybank/
│               └── transaction/
│                   ├── config/  # Cache config
│                   ├── controller/  # rest controller
│                   ├── dao/      # database operation 
│                   ├── domain/       # entity class
│                   ├── exception/   
│                   └── service/     # business logic

## Database design
### Transaction entity
| column | type|
|------|------|------|
| id | BIGINT | Primary ID |
| account_number | VARCHAR(50) |
| transaction_type | VARCHAR(20) |
| amount | DECIMAL(15,2)
| currency | VARCHAR(3)
| description | VARCHAR(500) |
| category | VARCHAR(50) |
| status | VARCHAR(20) |
| created_at | TIMESTAMP|
| updated_at | TIMESTAMP |

### Transaction type
- DEPOSIT
- WITHDRAWAL
- TRANSFER
- PAYMENT
- REFUND
- FEE
- INTEREST

## Test
### run unit test
```bash
mvn test -Dtest=TransactionServiceTest
mvn test -Dtest=TransactionDaoTest
```

### run stress test
```bash
mvn test -Dtest=ServiceStressTest
# 500 concurrency, 500,000 calls per API, my stress test machine has 4 intel cores
#error transaction ratio: 0.0, 
#createTransaction req/s:14143,  
#getAllTransactions req/s:70432, 
#updateTransaction req/s:14398,  
#deleteTransaction req/s:15934
```

## cache strategy
- **query cache**: take Caffeine as local cache
- **cache expiration time**: 30 mins
- **max cache nums**: 100000
