#!/bin/bash

set -eu 

set -o pipefail 

openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in privkey.pem -out privkey