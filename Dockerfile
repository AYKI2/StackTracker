# Dev stage
FROM gradle:8.13.0-jdk21-alpine AS dev
WORKDIR /app

# Кэширование зависимостей Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
COPY build.gradle settings.gradle gradle.properties ./
COPY . .

RUN chmod +x gradlew

#RUN gradle dependencies --no-daemon
RUN ./gradlew clean build -x test

# Копирование исходников
#COPY src src

# Запуск в dev-режиме с hot-reload
#ENTRYPOINT ["gradle", "bootRun", "--no-daemon", "--continuous", "-PspringProfile=dev"]

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=$PORT -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar build/libs/*.jar"]


