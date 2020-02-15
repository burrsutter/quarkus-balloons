#!/bin/bash

set -eu 

set -o pipefail 

mvn exec:java -Dexec.mainClass=com.redhat.developer.balloon.GenerateJwtToken -Dexec.classpathScope=test -Dexec.args="privkey 36000" 

echo "Create a JWT_TOKEN env var output from the above command"
