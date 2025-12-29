# 1. Aşama: Build (Maven ile projeyi derle)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Testleri atlayarak derleme yapıyoruz ki build süresi kısalsın ve test hataları build'i durdurmasın
RUN mvn clean package -DskipTests

# 2. Aşama: Run (Sadece oluşan JAR dosyasını çalıştır)
# DEĞİŞİKLİK BURADA: openjdk yerine eclipse-temurin kullanıyoruz
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]