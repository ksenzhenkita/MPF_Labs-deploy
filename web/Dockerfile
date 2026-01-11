# Етап 1: Збірка (Build stage)
# Використовуємо образ з Maven та Java 17 для компіляції
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Копіюємо весь проєкт (всі модулі), бо web залежить від core та persistence
COPY . .

# Запускаємо збірку саме веб-модуля з усіма залежностями
RUN mvn clean package -pl web -am -DskipTests

# Етап 2: Запуск (Runtime stage)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копіюємо тільки готовий JAR-файл з першого етапу
# Зверни увагу на шлях: web/target/...
COPY --from=build /app/web/target/web-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]