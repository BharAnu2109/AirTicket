# AirTicket - Comprehensive Microservices Architecture

A comprehensive Spring Boot microservices application for air ticket booking system featuring:

## 🏗️ Architecture Overview

### Microservices
- **API Gateway** (Port 8080) - Gateway with load balancing and circuit breakers
- **Eureka Server** (Port 8761) - Service discovery and registration  
- **User Service** (Port 8081) - User management and authentication
- **Flight Service** (Port 8082) - Flight search and management with caching
- **Booking Service** (Port 8083) - Reservation management with Saga pattern
- **Payment Service** (Port 8084) - Payment processing with Strategy pattern
- **Notification Service** (Port 8085) - Email/SMS notifications

### Infrastructure Components
- **Apache Kafka** (KRaft mode - without Zookeeper) - Event streaming
- **PostgreSQL** - Primary database
- **Redis** - Caching layer
- **Zipkin** - Distributed tracing

## 🚀 Key Features

### Event-Driven Architecture
- **Kafka Integration**: Event streaming with topics for booking, payment, and notifications
- **Choreography Pattern**: Services communicate through events
- **Event Sourcing**: Complete audit trail of all operations

### Orchestration & Choreography
- **Saga Pattern**: Distributed transaction management in booking process
- **State Machine**: Spring State Machine for saga orchestration
- **Compensation Logic**: Automatic rollback on failures

### Design Patterns Implementation
- **Strategy Pattern**: Payment processing (Credit Card, PayPal)
- **Observer Pattern**: Event notification system
- **Singleton Registry**: Service instance management
- **Repository Pattern**: Data access layer abstraction

### Advanced Data Structures
- **Trie**: Airport search autocomplete functionality
- **Graph**: Flight route optimization using Dijkstra's algorithm
- **Custom Collections**: Optimized for flight search operations

### Distributed Tracing
- **Zipkin Integration**: Complete request tracing across services
- **Correlation IDs**: Request tracking through the entire system
- **Performance Monitoring**: Latency and error tracking

### Caching Strategy
- **Redis Integration**: Flight data caching
- **Cache-aside Pattern**: Improved performance for frequent queries
- **TTL Management**: Automatic cache expiration

### Testing Strategy
- **Unit Tests**: Comprehensive test coverage
- **Integration Tests**: Service interaction testing
- **TestContainers**: Database and Kafka testing
- **Contract Testing**: API contract validation

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Database**: PostgreSQL 15
- **Message Broker**: Apache Kafka (KRaft mode)
- **Caching**: Redis 7
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Distributed Tracing**: Zipkin
- **Authentication**: JWT tokens
- **Documentation**: OpenAPI 3.0
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Java Version**: 17

## 🏃‍♂️ Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17+ (for local development)
- Maven 3.8+ (for local development)

### Running with Docker Compose
```bash
# Clone the repository
git clone <repository-url>
cd AirTicket

# Start all services
docker-compose up -d

# Check service health
docker-compose ps
```

### Building from Source
```bash
# Build all services
mvn clean package -DskipTests

# Start infrastructure only
docker-compose up -d kafka postgres redis zipkin eureka-server

# Run services locally
cd user-service && mvn spring-boot:run
cd flight-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## 📋 API Endpoints

### User Service (8081)
- `POST /api/v1/users/register` - Register new user
- `GET /api/v1/users/{id}` - Get user by ID
- `PUT /api/v1/users/{id}` - Update user

### Flight Service (8082)
- `GET /api/v1/flights/search` - Search flights
- `GET /api/v1/flights/{id}` - Get flight details
- `POST /api/v1/flights/{id}/reserve-seats` - Reserve seats

### Booking Service (8083)
- `POST /api/v1/bookings` - Create booking (starts Saga)
- `GET /api/v1/bookings/{id}` - Get booking details
- `PUT /api/v1/bookings/{id}/cancel` - Cancel booking

### Payment Service (8084)
- `POST /api/v1/payments/process` - Process payment
- `POST /api/v1/payments/{id}/refund` - Refund payment

## 🔄 Saga Pattern Flow

### Booking Process
1. **Seat Reservation**: Reserve seats on flight
2. **Payment Processing**: Process payment using selected strategy
3. **Notification**: Send confirmation email/SMS
4. **Completion**: Mark booking as completed

### Compensation Flow
- **Payment Failure**: Release reserved seats
- **Notification Failure**: Refund payment and release seats
- **Automatic Rollback**: All operations are compensated

## 🎯 Complex Scenarios Handled

### Concurrent Booking
- Optimistic locking for seat availability
- Race condition handling
- Distributed locks where needed

### Payment Processing
- Multiple payment gateways
- Retry mechanisms
- Idempotency handling

### System Resilience
- Circuit breakers
- Bulkhead isolation
- Graceful degradation

### Data Consistency
- Eventual consistency
- Saga pattern for distributed transactions
- Event sourcing for audit trail

## 📊 Monitoring & Observability

- **Health Checks**: Spring Actuator endpoints
- **Metrics**: Prometheus compatible metrics
- **Tracing**: Zipkin distributed tracing
- **Logging**: Structured logging with correlation IDs

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Test with TestContainers
mvn test -Dtest=*IntegrationTest
```

## 🔧 Configuration

Each service uses environment-specific configurations:
- `application.yml` - Default configuration
- Environment variables for Docker deployment
- Spring Profiles for different environments

## 🚢 AWS Deployment Ready

The application includes:
- ECS/EKS deployment configurations
- CloudFormation templates
- Application Load Balancer setup
- RDS and ElastiCache integration
- CloudWatch monitoring

## 📝 Development Notes

### Adding New Services
1. Create new Maven module
2. Add to parent POM
3. Configure Eureka registration
4. Add to Docker Compose
5. Update API Gateway routes

### Event Handling
- All events extend `BaseEvent`
- Use `@KafkaListener` for event consumption
- Implement idempotency for event processing

### Database Migrations
- Use Flyway for database versioning
- Include sample data for development

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Submit pull request

## 📄 License

This project is licensed under the MIT License.