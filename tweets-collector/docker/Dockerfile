FROM eclipse-temurin:21-jdk AS build

WORKDIR /tweets-collector

COPY . .

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre

RUN useradd -ms /bin/bash tweets-collector-app-user

WORKDIR /tweets-collector

COPY --from=build /tweets-collector/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER  tweets-collector-app-user

COPY --from=build /tweets-collector/target/tweets-collector-*.jar tweets-collector.jar

ENTRYPOINT ["/entrypoint.sh"]
