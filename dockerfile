FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

# Если есть gradlew
RUN if [ -f "gradlew" ]; then chmod +x gradlew && ./gradlew build -x test; fi

# Если нет gradlew, используем установленный Gradle
RUN if [ ! -f "gradlew" ]; then apk add --no-cache gradle && gradle build -x test; fi

CMD ["java", "-jar", "build/libs/SSCC_Gen-1.0-SNAPSHOT.jar"]
