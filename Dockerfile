FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем всё содержимое проекта
COPY . .

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Компилируем проект
RUN ./gradlew build -x test

# Запускаем приложение
CMD ["java", "-jar", "build/libs/SSCC_Gen-1.0-SNAPSHOT.jar"]
