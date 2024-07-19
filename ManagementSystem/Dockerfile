FROM maven:3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

#run stage
FROM openjdk:17-jdk-slim
COPY --from=build /target/ManagementSystem-0.0.1-SNAPSHOT.jar demo.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]
