# Dev stage
FROM gradle:8.13.0-jdk21-alpine AS dev
WORKDIR /app

# Кэширование зависимостей Gradle
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle gradle
RUN gradle dependencies --no-daemon

# Копирование исходников
COPY src src

# Запуск в dev-режиме с hot-reload
ENTRYPOINT ["gradle", "bootRun", "--no-daemon", "--continuous", "-PspringProfile=dev"]