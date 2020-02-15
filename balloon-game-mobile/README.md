# Red Hat Keynote Mobile Application

## Versions
```
nvm use v8.16.0

node -v
v8.16.0

npm -v
6.4.1
```

## Run locally
First install
```
npm install
```

Then run
```
npm start
```

To run locally using production environment
```
ng serve -prod
```

## Build
```
npm run build
```

This generates a dist directory that can be deployed to a server.

## Docker build
````
CLIENT_IMG_VER=1.0.1

docker build  \
-f Dockerfile \
-t dev.local/burrsutter/balloon-game-client:$CLIENT_IMG_VER \
.

````
