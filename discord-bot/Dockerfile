FROM arm32v7/adoptopenjdk:11.0.10_9-jdk-hotspot-focal
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080
COPY build/libs/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar