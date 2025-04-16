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

ENV DB_URL=postgres://ueg19uj1f8p2s8:p3ecb36034daad1fdda1048a42186c347863c7d1f4b41237e69e4b5f301a2f542@cc0gj7hsrh0ht8.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d3g0ovp149kql1
ENV DB_USERNAME=ueg19uj1f8p2s8
ENV DB_PASSWORD=p3ecb36034daad1fdda1048a42186c347863c7d1f4b41237e69e4b5f301a2f542

CMD ["sh", "-c", "java -Dserver.port=$PORT -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar build/libs/*.jar"]


