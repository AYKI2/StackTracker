# Dev stage
FROM gradle:8.13.0-jdk21-alpine AS dev
WORKDIR /app

# Кэширование зависимостей Gradle
#COPY build.gradle settings.gradle gradle.properties ./
COPY . .

RUN chmod +x gradlew

#RUN gradle dependencies --no-daemon
RUN ./gradlew build -x test

# Копирование исходников
#COPY src src

# Запуск в dev-режиме с hot-reload
#ENTRYPOINT ["gradle", "bootRun", "--no-daemon", "--continuous", "-PspringProfile=dev"]

EXPOSE 8080

CMD ["java", "-Dserver.port=$PORT", "build/libs/StockTracker-0.0.1-SNAPSHOT.jar", "-jar", "app.jar"]


