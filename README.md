# TaskFlow API — Spring Boot 3 / Java 21

API REST (JWT) : utilisateurs, projets, tâches. Chaque utilisateur ne voit que ses propres projets.

## Lancer en local (aucun prérequis : base H2 en mémoire)

```bash
mvn spring-boot:run          # http://localhost:8080
mvn test                     # tests d'intégration
```

Swagger UI : http://localhost:8080/swagger-ui.html (bouton **Authorize** → colle le token).
Santé : http://localhost:8080/actuator/health

## Endpoints

| Méthode | URL | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | non | `{name,email,password}` → `{token,name,email}` (201) |
| POST | `/api/auth/login` | non | `{email,password}` → `{token,name,email}` |
| GET | `/api/auth/me` | oui | utilisateur connecté |
| GET/POST | `/api/projects` | oui | liste / création `{name,description}` |
| GET/PUT/DELETE | `/api/projects/{id}` | oui | détail / modif / suppression (cascade sur les tâches) |
| GET | `/api/projects/{id}/tasks?status=TODO` | oui | tâches (filtre optionnel) |
| POST | `/api/projects/{id}/tasks` | oui | `{title,description,status,priority,dueDate}` |
| PUT | `/api/projects/{id}/tasks/{taskId}` | oui | modification |
| PATCH | `/api/projects/{id}/tasks/{taskId}/status` | oui | `{status}` |
| DELETE | `/api/projects/{id}/tasks/{taskId}` | oui | suppression |

Enums : `status` = `TODO | IN_PROGRESS | DONE` ; `priority` = `LOW | MEDIUM | HIGH` ; `dueDate` = `yyyy-MM-dd`.
Erreurs : `{timestamp,status,message,errors:{champ:message}}` (400 validation, 401, 404, 409).

Test rapide :
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"name":"Walid","email":"w@x.com","password":"password123"}' | jq -r .token)
curl -s localhost:8080/api/projects -H "Authorization: Bearer $TOKEN"
```

## Test "type prod" en local (PostgreSQL)

```bash
docker compose up --build
```

## Variables d'environnement (profil `prod`)

| Variable | Exemple |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://<serveur>.postgres.database.azure.com:5432/taskflow?sslmode=require` |
| `DB_USER` / `DB_PASSWORD` | identifiants PostgreSQL |
| `JWT_SECRET` | chaîne aléatoire ≥ 32 caractères (obligatoire en prod) |
| `CORS_ALLOWED_ORIGINS` | `https://<ton-front>.azurestaticapps.net` (séparées par des virgules) |

## Déploiement Azure (App Service + PostgreSQL Flexible Server)

Remplace les `<...>` (les noms doivent être uniques).

```bash
az login
az group create -n rg-taskflow -l francecentral

# 1. Base PostgreSQL
az postgres flexible-server create -g rg-taskflow -n <pg-name> -l francecentral \
  --admin-user taskadmin --admin-password '<MotDePasseFort>' \
  --sku-name Standard_B1ms --tier Burstable --public-access 0.0.0.0
az postgres flexible-server db create -g rg-taskflow -s <pg-name> -d taskflow

# 2. App Service (Linux, Java 21)
az appservice plan create -g rg-taskflow -n plan-taskflow --is-linux --sku B1
az webapp create -g rg-taskflow -p plan-taskflow -n <api-name> --runtime "JAVA:21-java21"

# 3. Configuration
az webapp config appsettings set -g rg-taskflow -n <api-name> --settings \
  SPRING_PROFILES_ACTIVE=prod \
  DB_URL="jdbc:postgresql://<pg-name>.postgres.database.azure.com:5432/taskflow?sslmode=require" \
  DB_USER=taskadmin DB_PASSWORD='<MotDePasseFort>' \
  JWT_SECRET="$(openssl rand -base64 48)" \
  CORS_ALLOWED_ORIGINS="https://<ton-front>.azurestaticapps.net"

# 4. Build + déploiement du jar
mvn clean package -DskipTests
az webapp deploy -g rg-taskflow -n <api-name> --src-path target/taskflow-api.jar --type jar
```

Ensuite : dans le portail Azure → App Service → **Health check**, mets `/actuator/health`.
API en ligne : `https://<api-name>.azurewebsites.net/swagger-ui.html`.

Notes :
- `ddl-auto: update` crée les tables au démarrage : pratique pour démarrer. Pour une vraie prod, passe à Flyway/Liquibase.
- Alternative conteneur : build l'image avec le `Dockerfile`, pousse-la sur Azure Container Registry, déploie sur Container Apps ou App Service (`WEBSITES_PORT=8080`).
- Quand le front est déployé, mets son URL exacte dans `CORS_ALLOWED_ORIGINS`.
