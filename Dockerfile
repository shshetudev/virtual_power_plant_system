FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar vpp_service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "vpp_service.jar"]