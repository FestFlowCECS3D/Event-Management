# ==========================================
# Step 1: Build the frontend
# ==========================================
FROM node:18 AS frontend-build
WORKDIR /app/frontend

# Copy frontend source files from the repository root
# (Replace 'eventisma-frontend' if your frontend folder has a different name)
COPY eventisma-frontend/ ./

RUN npm install
RUN npm run build

# ==========================================
# Step 2: Build the backend + inject frontend
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy backend files
COPY eventisma-backend/pom.xml .
COPY eventisma-backend/src ./src

# Copy compiled static frontend files into Spring Boot's static resources directory
# (If your frontend framework outputs to 'build' instead of 'dist', change 'dist' to 'build')
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# ==========================================
# Step 3: Run the app using Temurin JRE
# ==========================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the exact snapshot JAR generated from Step 2
COPY --from=build /app/target/eventisma-backend-1.0.0-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
