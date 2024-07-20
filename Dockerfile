FROM openjdk:21-jdk

WORKDIR /app

COPY build/libs/mn-stock-broker-0.1-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]