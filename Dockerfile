FROM amazoncorretto:21 AS builder
WORKDIR /app
COPY . .
ENV PORT 9090
RUN chmod +x ./gradlew && ./gradlew bootJar

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/StockTracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
