# Dev stage
#FROM gradle:8.13.0-jdk21-alpine AS dev
#WORKDIR /app

# Кэширование зависимостей Gradle
#COPY build.gradle settings.gradle gradle.properties ./
#COPY gradle gradle
#RUN gradle dependencies --no-daemon
#
## Копирование исходников
#COPY src src
#
## Запуск в dev-режиме с hot-reload
#ENTRYPOINT ["gradle", "bootRun", "--no-daemon", "--continuous", "-PspringProfile=dev"]

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY ./build/libs/StockTracker-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8080

CMD ["java", "-Dserver.port=$PORT", "-Dspring.profiles.active=prod", "-jar", "app.jar"]


