# Base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /services/gateway-service

# Copy the built jar into the container (assuming you build with Maven)
COPY target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
