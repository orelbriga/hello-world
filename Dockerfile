FROM gradle:7.5.1-jdk11-alpine
RUN mkdir hello-world-app
WORKDIR hello-world-app
ADD build/libs/hello-world-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
