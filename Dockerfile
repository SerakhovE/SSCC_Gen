FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test
CMD ["java", "-jar", "build/libs/SSCC_Gen-1.0-SNAPSHOT.jar"]
