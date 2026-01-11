# Етап збірки (можна використовувати той самий образ, що і в GitHub Actions)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копіюємо JAR файл веб-модуля
# Назва має збігатися з artifactId у pom.xml веб-модуля
COPY target/web-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]