FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -q package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/classes /app/classes
ENV ARENA_PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "/app/classes", "io.orhanyc.arena.ArenaApplication", "serve"]
