#!/bin/bash
set -e

echo "Starting Pokemon API in PRODUCTION mode..."

docker compose up -d pokemon-api

echo "Waiting for health check..."
sleep 10

if docker compose ps | grep -q "healthy"; then
    echo "Pokemon API is running and healthy"
    echo "API available at: http://localhost:8080"
    echo "Swagger UI: http://localhost:8080/swagger-ui.html"
else
    echo "Warning: Container may still be starting. Check logs with: docker compose logs -f"
fi
