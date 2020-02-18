docker build -f src/main/docker/Dockerfile.openjdk -t gcr.io/devnexus-balloons/balloon-game-server .
docker push gcr.io/devnexus-balloons/balloon-game-server
