# 🎾 Tennis

Ce projet est une application **Spring Boot** permettant de gérer des **joueurs de tennis** et leurs **statistiques** via une API REST.

---

## ⚙️ Comment lancer l'application

1. **Cloner le dépôt :**

   ```bash
   git clone https://github.com/horessam/latelier-tennis.git
   cd latelier-tennis
   ```

2. **Lancer l'application avec Maven :**

   ```bash
   ./mvnw spring-boot:run
   ```

   (ou `mvn spring-boot:run` si Maven est déjà installé)

3. **Accéder à l'API en local :**

   ```
   http://localhost:8080/api/players
   ```

---

## 🧪 Comment tester l'application

Tu peux utiliser **Postman**, **cURL**, ou tout autre outil pour tester les endpoints.

### Exemples :

#### 🔹 Récupérer tous les joueurs

```bash
GET http://localhost:8080/api/players
```

#### 🔹 Créer un joueur

```bash
POST http://localhost:8080/api/players
Content-Type: application/json

{
  "firstname": "Rafael",
  "lastname": "Nadal",
  "country": "Spain",
  "rank": 1,
  "winRatio": 0.85
}
```

#### 🔹 Obtenir le classement des joueurs

```bash
GET http://localhost:8080/api/players/ranking
```

#### 🔹 Supprimer un joueur

```bash
DELETE http://localhost:8080/api/players/1
```
