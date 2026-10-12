FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests
RUN cp "$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/app.jar ./app.jar

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]
