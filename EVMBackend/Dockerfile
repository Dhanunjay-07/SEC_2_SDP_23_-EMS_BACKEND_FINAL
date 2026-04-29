# Multi-stage build for Spring Boot with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/EVMBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Railway will override with $PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
CMD java -jar app.jar --server.port=${PORT:-8080} --spring.config.name=application-prod
