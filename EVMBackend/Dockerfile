# Multi-stage build for Spring Boot with Java 21
# Cache invalidation: Force rebuild
ARG BUILDKIT_INLINE_CACHE=1
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
RUN echo "Forcing rebuild cache invalidation"
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
# Force rebuild - timestamp: 2026-04-29 15:25
COPY src src

# Build the application
RUN echo "Build timestamp: $(date)" && mvn clean package -DskipTests

# List what jars were created
RUN echo "=== JARS IN TARGET ===" && ls -lah /app/target/*.jar 2>/dev/null || echo "No jars found!"
RUN echo "=== ALL TARGET CONTENTS ===" && ls -lah /app/target/ | head -20

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR from builder - find the executable jar (excludes original)
COPY --from=builder /app/target target

# Verify JAR exists and is readable
RUN ls -lah /app/target/*.jar && file /app/target/*.jar || echo "No jars found"

# Expose port (Railway will override with $PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar /app/target/EVMBackend-0.0.1-SNAPSHOT.jar --server.port=${PORT:-8080} --spring.config.name=application-prod"]
