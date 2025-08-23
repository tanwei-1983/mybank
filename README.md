# 银行交易管理系统

一个基于Spring Boot的银行交易管理系统，提供完整的交易CRUD操作、分页查询、缓存机制和容器化部署支持。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **MyBatis 3.0.2**
- **H2 内存数据库**
- **Caffeine 缓存**
- **Maven**
- **Docker & Kubernetes**

## 功能特性

### 核心功能
- ✅ 创建交易记录
- ✅ 查询交易详情
- ✅ 更新交易信息
- ✅ 删除交易记录
- ✅ 分页查询交易列表
- ✅ 按账户号码筛选交易
- ✅ 按交易类型筛选交易

### 技术特性
- ✅ RESTful API设计
- ✅ 数据验证和异常处理
- ✅ 缓存机制（Caffeine）
- ✅ 分页和排序
- ✅ 单元测试和集成测试
- ✅ Docker容器化
- ✅ Kubernetes部署配置
- ✅ 前端Web界面

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- Docker (可选)
- Kubernetes (可选)

### 本地运行

1. **克隆项目**
```bash
git clone <repository-url>
cd mybank
```

2. **编译项目**
```bash
mvn clean compile
```

3. **运行测试**
```bash
mvn test
```

4. **启动应用**
```bash
mvn spring-boot:run
```

5. **访问应用**
- Web界面: http://localhost:8080
- API文档: http://localhost:8080/api/transactions
- H2控制台: http://localhost:8080/h2-console
- 健康检查: http://localhost:8080/api/actuator/health

### Docker运行

1. **构建镜像**
```bash
docker build -t transaction-management .
```

2. **运行容器**
```bash
docker run -p 8080:8080 transaction-management
```

3. **使用Docker Compose**
```bash
docker-compose up -d
```

### Kubernetes部署

1. **构建镜像**
```bash
docker build -t transaction-management .
```

2. **部署到Kubernetes**
```bash
kubectl apply -f k8s/deployment.yaml
```

## API文档

### 基础URL
```
http://localhost:8080/api
```

### 交易管理API

#### 1. 创建交易
```http
POST /transactions
Content-Type: application/json

{
  "accountNumber": "1234567890123456",
  "transactionType": "DEPOSIT",
  "amount": 1000.00,
  "currency": "CNY",
  "description": "工资收入",
  "category": "SALARY"
}
```

#### 2. 获取交易详情
```http
GET /transactions/{id}
```

#### 3. 根据交易ID获取交易
```http
GET /transactions/by-transaction-id/{transactionId}
```

#### 4. 更新交易
```http
PUT /transactions/{id}
Content-Type: application/json

{
  "accountNumber": "1234567890123456",
  "transactionType": "DEPOSIT",
  "amount": 1500.00,
  "currency": "CNY",
  "description": "更新后的描述",
  "category": "SALARY"
}
```

#### 5. 删除交易
```http
DELETE /transactions/{id}
```

#### 6. 分页查询所有交易
```http
GET /transactions?page=1&size=20&sortBy=createdAt&sortOrder=DESC
```

#### 7. 按账户号码查询交易
```http
GET /transactions/account/{accountNumber}?page=1&size=20
```

#### 8. 按交易类型查询交易
```http
GET /transactions/type/{transactionType}?page=1&size=20
```

### 响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 具体数据
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

## 数据库设计

### 交易表 (transactions)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| transaction_id | VARCHAR(50) | 交易ID（业务唯一标识） |
| account_number | VARCHAR(50) | 账户号码 |
| transaction_type | VARCHAR(20) | 交易类型 |
| amount | DECIMAL(15,2) | 交易金额 |
| currency | VARCHAR(3) | 货币类型 |
| description | TEXT | 交易描述 |
| category | VARCHAR(50) | 交易类别 |
| status | VARCHAR(20) | 交易状态 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 交易类型
- DEPOSIT: 存款
- WITHDRAWAL: 取款
- TRANSFER: 转账
- PAYMENT: 支付
- REFUND: 退款
- FEE: 手续费
- INTEREST: 利息

## 测试

### 运行所有测试
```bash
mvn test
```

### 运行单元测试
```bash
mvn test -Dtest=TransactionServiceTest
```

### 运行集成测试
```bash
mvn test -Dtest=TransactionControllerIntegrationTest
```

### 性能测试
```bash
# 使用JMeter或其他工具进行压力测试
# 示例：1000并发用户，持续5分钟
```

## 监控和健康检查

### 健康检查端点
- `/api/actuator/health` - 应用健康状态
- `/api/actuator/info` - 应用信息
- `/api/actuator/metrics` - 性能指标
- `/api/actuator/caches` - 缓存状态

### 日志配置
应用使用SLF4J + Logback进行日志记录，日志级别可在`application.yml`中配置。

## 缓存策略

- **交易查询缓存**: 使用Caffeine本地缓存
- **缓存过期时间**: 30分钟
- **最大缓存条目**: 1000条
- **缓存统计**: 启用缓存命中率统计

## 部署配置

### 生产环境配置
```yaml
spring:
  profiles: prod
  datasource:
    url: jdbc:mysql://localhost:3306/transactiondb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  cache:
    type: redis
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

### 环境变量
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码
- `REDIS_HOST`: Redis主机地址
- `REDIS_PORT`: Redis端口
- `JAVA_OPTS`: JVM参数

## 故障排除

### 常见问题

1. **端口占用**
```bash
# 查看端口占用
netstat -ano | findstr :8080
# 杀死进程
taskkill /PID <进程ID> /F
```

2. **数据库连接失败**
- 检查H2数据库是否正常启动
- 验证数据库连接配置

3. **缓存问题**
- 清除应用缓存: 重启应用
- 检查缓存配置

### 日志查看
```bash
# 查看应用日志
tail -f logs/application.log
```

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱: your-email@example.com
- 项目地址: https://github.com/your-username/mybank

---

**注意**: 这是一个演示项目，仅用于学习和测试目的。在生产环境中使用前，请确保进行充分的安全审查和测试。
