# Stage 1: Build
FROM gradle:9.4-jdk17-alpine as build

WORKDIR /src

COPY . .

RUN gradle build

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the application JAR from the build stage
COPY --from=build /src/build/libs/*.jar retronetmc-server.jar
COPY --from=build /src/entrypoint.sh entrypoint.sh

# Expose HTTPS port
EXPOSE 5001

# Health check to verify the application is running
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3     CMD curl --insecure -f https://localhost:5001/ || exit 1

RUN chmod +x ./entrypoint.sh

# Run the application
ENTRYPOINT ["./entrypoint.sh"]
