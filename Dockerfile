# Use a lightweight OpenJDK base image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file from your build directory to the container
COPY transactions-api-impl/target/*.jar app.jar

# Expose the port the Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
