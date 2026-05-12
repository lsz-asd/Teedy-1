#!/bin/bash
# ============================================================
# Local CI/CD Pipeline Script (simulates Jenkins pipeline)
# Practice 10 - CI/CD with Jenkins and Docker
# ============================================================
set -e

DOCKER_HUB_USERNAME="${DOCKER_HUB_USERNAME:-lsz-asd}"
DOCKER_HUB_REPO="${DOCKER_HUB_REPO:-lsz-asd/teedy}"
DOCKER_IMAGE_TAG="${DOCKER_IMAGE_TAG:-latest}"

echo "========================================"
echo " Stage 1: Build Docker Image"
echo "========================================"
docker build -t ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG} .
docker tag ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG} ${DOCKER_HUB_REPO}:latest
echo "[OK] Docker image built: ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"

echo ""
echo "========================================"
echo " Stage 2: Push to Docker Hub"
echo "========================================"
if [ -z "${DOCKER_HUB_PASSWORD}" ]; then
    echo "Enter Docker Hub password for ${DOCKER_HUB_USERNAME}:"
    docker login -u ${DOCKER_HUB_USERNAME}
else
    echo "${DOCKER_HUB_PASSWORD}" | docker login -u ${DOCKER_HUB_USERNAME} --password-stdin
fi
docker push ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}
docker push ${DOCKER_HUB_REPO}:latest
docker logout
echo "[OK] Docker image pushed to Docker Hub"

echo ""
echo "========================================"
echo " Stage 3: Deploy 3 Containers"
echo "========================================"
echo "Stopping and removing old containers..."
docker stop teedy-8082 teedy-8083 teedy-8084 2>/dev/null || true
docker rm teedy-8082 teedy-8083 teedy-8084 2>/dev/null || true

echo "Starting containers on ports 8082, 8083, 8084..."
docker run -d --name teedy-8082 -p 8082:8080 ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}
docker run -d --name teedy-8083 -p 8083:8080 ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}
docker run -d --name teedy-8084 -p 8084:8080 ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}

echo ""
echo "========================================"
echo " Deployment Complete!"
echo "========================================"
echo "Containers running:"
docker ps --filter "name=teedy" --format "table {{.Names}}\t{{.Ports}}\t{{.Status}}"
echo ""
echo "Access the app at:"
echo "  http://localhost:8082"
echo "  http://localhost:8083"
echo "  http://localhost:8084"
