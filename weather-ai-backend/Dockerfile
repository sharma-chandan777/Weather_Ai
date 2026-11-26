
# ==================================
# STAGE 1: BUILD SPRING BOOT APP
# ==================================

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -B clean package -DskipTests


# ==================================
# STAGE 2: RUN APPLICATION
# ==================================

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy executable JAR
COPY --from=build /app/target/*.jar app.jar

# Render application port
EXPOSE 8080

# Memory optimized Java startup
ENTRYPOINT ["java", \
    "-Xms64m", \
    "-Xmx220m", \
    "-XX:MaxMetaspaceSize=96m", \
    "-XX:ReservedCodeCacheSize=32m", \
    "-XX:+UseSerialGC", \
    "-Xss512k", \
    "-XX:ActiveProcessorCount=1", \
    "-jar", \
    "app.jar"]
