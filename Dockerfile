FROM gradle:jdk11
RUN mkdir /hello-world-app
WORKDIR /hello-world-app
COPY build/libs/hello-world-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

