# Etapa 1: Construccion (Maven + JDK 21 de Eclipse Temurin)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y descargamos dependencias (esto optimiza el cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el codigo fuente y construimos el jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecucion (Runtime de Java 21)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
# Usamos el comodin para que no importe si la version cambia a 0.0.2 despues
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto estandar de Spring Boot
EXPOSE 8080

# Definimos variables de entorno por defecto (Render las sobrescribira)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Arrancamos la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]