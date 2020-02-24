#!/bin/bash

kubectl -n default delete deployment configuration-service-sb
kubectl -n default delete service configuration-service-sb
kubectl -n default delete route configuration-service-sb