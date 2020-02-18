npm run build
docker build --compress -t gcr.io/devnexus-balloons/balloon-game-mobile .
docker push gcr.io/devnexus-balloons/balloon-game-mobile
