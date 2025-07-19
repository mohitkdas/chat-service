FROM openjdk:17-jdk-slim
COPY *.jar chat.jar
ENTRYPOINT ["java", "-jar", "/chat.jar"]