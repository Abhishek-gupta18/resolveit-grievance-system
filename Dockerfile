# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy only the Maven metadata first so dependency resolution can be cached.
COPY pom.xml .

# Copy the application source.
COPY src ./src

# Build the Spring Boot application.
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV JAVA_OPTS=""

# Run as a non-root user.
RUN useradd --create-home --uid 1001 appuser

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
