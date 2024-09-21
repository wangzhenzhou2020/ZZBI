# Docker 镜像构建
#
#
FROM maven:3.8.1-jdk-8-slim as builder

## Copy local code to the container image.
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests


WORKDIR /app
COPY ./target/yubi-backend-dupli-0.0.1-SNAPSHOT.jar .

# Run the web service on container startup.
#CMD ["java","-jar","/app/target/yubi-backend-dupli-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
CMD ["java","-jar","/app/yubi-backend-dupli-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]