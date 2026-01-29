#!/bin/bash
set -e

echo "Building and starting Pokemon API..."

docker compose up -d --build

echo ""
echo "Waiting for services to be healthy..."
sleep 5

echo ""
echo "Services started successfully!"
echo ""
echo "Frontend:   http://localhost:3000"
echo "API:        http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Logs: docker compose logs -f"
