FROM openjdk:17-jdk-slim

ENV JAVA_OPTS="-Xmx128m -Xms64m"

COPY *.jar chat.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /chat.jar"]