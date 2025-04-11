FROM gradle:8.13.0-jdk21  AS dev

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

COPY src ./src

EXPOSE 8080

ENTRYPOINT ["gradle", "bootRun", "--no-daemon", "-g", "/tmp/.gradle"]
