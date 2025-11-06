# Étape 1 : Build du projet
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copie du pom.xml et téléchargement des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie du code source et compilation
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copie du jar depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Expose le port dynamique fourni par Render
ENV PORT=8080
EXPOSE 8080

# Commande de lancement
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75 -Dserver.port=${PORT} -jar /app.jar"]
