FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests
RUN cp "$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/app.jar ./app.jar

EXPOSE 8081

CMD ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8081} -jar app.jar"]
