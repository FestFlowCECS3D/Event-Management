# Step 1: Build the app using Maven + Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run the built app using a lightweight Java runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/eventisma-backend-1.0.0-SNAPSHOT.jar app.jar

# Render tells us which port to listen on via this variable
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
