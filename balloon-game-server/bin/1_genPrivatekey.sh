#!/bin/bash

set -eu 

set -o pipefail 

openssl genrsa -out privkey.pem 4096