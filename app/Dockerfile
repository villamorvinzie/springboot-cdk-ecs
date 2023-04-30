FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG WEATHERSTACK_ACCESS_KEY
ENV WEATHERSTACK_ACCESS_KEY=$WEATHERSTACK_ACCESS_KEY
COPY target/demo-0.0.1-SNAPSHOT.jar springbootcdkecs.jar
EXPOSE 8080

# For Spring-Boot application, this entrypoint reduces Tomcat startup time.
ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom -jar springbootcdkecs.jar