# ====== build stage ======
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Спочатку копіюємо тільки pom'и, щоб прогріти кеш залежностей
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml

RUN mvn -B -q dependency:go-offline

# Тепер копіюємо увесь код
COPY core core
COPY persistence persistence
COPY web web

# Збираємо тільки web-модуль, підтягуючи залежні (core, persistence)
RUN mvn -pl web -am -B package -DskipTests

# ====== runtime stage ======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копіюємо зібраний JAR web-модуля
COPY --from=build /app/web/target/*.jar app.jar

EXPOSE 8080

# Можна передавати додаткові параметри JVM через JAVA_OPTS
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
