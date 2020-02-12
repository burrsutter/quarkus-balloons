#!/bin/bash

set -eu 

set -o pipefail 

openssl rsa -in privkey.pem -pubout -out publickey
