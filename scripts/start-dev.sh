#!/bin/bash
set -e

echo "Starting Pokemon API in DEVELOPMENT mode..."
echo "Debug port: 5005"

docker compose --profile dev up -d pokemon-api-dev

echo ""
echo "API available at: http://localhost:8080"
echo "Debug port: localhost:5005"
echo "H2 Console: http://localhost:8080/h2-console"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Logs: docker compose logs -f pokemon-api-dev"
