#!/bin/bash

set -eu 

set -o pipefail 

curl -v -H "Authorization: Bearer $JWT_TOKEN" localhost:8080/a/start