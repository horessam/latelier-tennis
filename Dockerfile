# Étape 1 : build avec Maven (pas de wrapper requis)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache des dépendances
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build de l'appli
COPY src ./src
RUN mvn -q -DskipTests clean package

# Étape 2 : image d'exécution légère
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copie du jar final
COPY --from=build /app/target/*.jar app.jar

# Render fournit $PORT -> on s'y branche
ENV PORT=8080
EXPOSE 8080

# Limite mémoire + port dynamique pour Render
ENTRYPOINT ["sh","-c","java -XX:MaxRAMPercentage=75 -Dserver.port=${PORT} -jar /app.jar"]
