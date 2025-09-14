#!/bin/bash

# Build script for AirTicket microservices

echo "Building AirTicket Microservices..."

# Build parent project first
echo "Building parent project..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Parent project built successfully"
else
    echo "❌ Parent project build failed"
    exit 1
fi

# Build common module
echo "Building common module..."
cd common && mvn clean compile -q && cd ..

if [ $? -eq 0 ]; then
    echo "✅ Common module built successfully"
else
    echo "❌ Common module build failed"
    exit 1
fi

# List of services to build
services=("eureka-server" "user-service" "flight-service" "booking-service" "payment-service" "notification-service" "api-gateway")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd "$service" && mvn clean compile -q && cd ..
    
    if [ $? -eq 0 ]; then
        echo "✅ $service built successfully"
    else
        echo "❌ $service build failed"
    fi
done

echo "Build process completed!"
echo ""
echo "To start the application:"
echo "1. Start infrastructure: docker-compose up -d kafka postgres redis zipkin"
echo "2. Start services: docker-compose up -d"
echo "3. Access API Gateway: http://localhost:8080"
echo "4. Access Eureka: http://localhost:8761"
echo "5. Access Zipkin: http://localhost:9411"