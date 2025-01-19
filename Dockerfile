# Use a valid Maven image with JDK 17
FROM maven:3.9.3-eclipse-temurin-17 AS builder

# Set the working directory for Maven
WORKDIR /app

# Copy the pom.xml and dependencies to the container
COPY pom.xml .
COPY src ./src

# Run Maven package to build the JAR
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image for the runtime
FROM openjdk:17-jdk-slim

# Set the working directory for the application
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8081
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
