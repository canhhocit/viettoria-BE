FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY maven-settings-render.xml /tmp/maven-settings-render.xml
COPY src ./src
RUN mvn -s /tmp/maven-settings-render.xml -B -DskipTests package \
    || mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --chown=10001:10001 --from=build /workspace/target/viettoria-be-1.0.0.jar /app/viettoria-be.jar

USER 10001:10001
EXPOSE 10000
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/viettoria-be.jar"]
