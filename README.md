# Pokemon API

A Spring Boot REST API for Pokemon data with local storage capabilities.

## Screenshots

<table>
  <tr>
    <td align="center"><strong>Pokemon List</strong></td>
    <td align="center"><strong>Pokemon Detail</strong></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/d7894ae6-71d6-45c6-9fae-98ea9c32d143" alt="Pokemon List" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/24e88d84-f30a-4fb3-80ce-5bcbe1887156" alt="Pokemon Detail" width="400"/></td>
  </tr>
  <tr>
    <td align="center"><strong>Local Collection</strong></td>
    <td align="center"><strong>Local Collection</strong></td>
    <td align="center"><strong>Swagger</strong></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/f6226386-6889-4182-b5a2-b56e5c163d65" alt="Local Collection" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/3a6ac0a8-d1ee-4504-bea9-05ff77142fb6" alt="Swagger API" width="400"/></td>
    <td><img width="1598" height="982" alt="image" src="https://github.com/user-attachments/assets/6d48b286-1cc0-4901-b29b-dd8ee3b91b74" /></td>

  </tr>
</table>

## Features

- List Pokemon with pagination and caching
- Pokemon detail with evolutions
- Copy Pokemon to local H2 database
- Extensible attributes (multi-language names, custom fields)

## Quick Start

### Prerequisites

- Docker and Docker Compose

### Run with Single Command

```bash
docker compose up --build
```

Or use the provided script:

```bash
./scripts/start-prod.sh
```

This starts both the API and frontend:
- **Frontend**: http://localhost:3000
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Local Development (without Docker)

```bash
# Terminal 1: Backend
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2: Frontend
cd frontend
npm install
npm run dev
```

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/api-docs

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/pokemon?page=0&size=20` | List Pokemon (cached) |
| GET | `/api/v1/pokemon/{id}` | Pokemon detail with evolutions |
| POST | `/api/v1/local-pokemon/{pokemonId}` | Copy from PokeAPI to H2 |
| GET | `/api/v1/local-pokemon/{id}` | Get local Pokemon |
| PUT | `/api/v1/local-pokemon/{id}` | Update local Pokemon |
| GET | `/api/v1/local-pokemon` | List all local Pokemon |

## Example Usage

### List Pokemon
```bash
curl http://localhost:8080/api/v1/pokemon?page=0&size=10
```

### Get Pokemon Detail
```bash
curl http://localhost:8080/api/v1/pokemon/1
```

### Copy Pokemon to Local Database
```bash
curl -X POST http://localhost:8080/api/v1/local-pokemon/1 \
  -H "Content-Type: application/json" \
  -d '{"attributes": {"name_jp": "フシギダネ", "name_es": "Bulbasaur"}}'
```

### Update Local Pokemon
```bash
curl -X PUT http://localhost:8080/api/v1/local-pokemon/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "My Bulbasaur", "attributes": {"nickname": "Bulby"}}'
```

## Configuration

See [JVM_CONFIG.md](JVM_CONFIG.md) for memory tuning.
See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common issues.

## Architecture

See [DECISIONS.md](DECISIONS.md) for design decisions.

## Running Tests

```bash
cd backend
./mvnw test
```

## Project Structure

```
pokefoobar/
├── backend/           # Spring Boot API
├── frontend/          # React application
├── scripts/           # Startup scripts
└── docker-compose.yml
```
