FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем исходный код
COPY . .

# Устанавливаем Gradle и собираем проект
RUN apk add --no-cache gradle
RUN gradle build -x test

# Запускаем приложение
CMD ["java", "-jar", "build/libs/SSCC_Gen-1.0-SNAPSHOT.jar"]
