FROM openjdk:17.0.1-jdk-slim

WORKDIR  /app

COPY ./build/libs/Reminder-1.0.0.jar reminder-app.jar

ENTRYPOINT ["java", "-jar", "reminder-app.jar"]