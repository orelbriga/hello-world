FROM gradle:7.5.1-jdk11-jammy
RUN mkdir /hello-world-app
WORKDIR /hello-world-app
ADD build/libs/hello-world-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

