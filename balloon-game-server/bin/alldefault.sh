#!/bin/bash
set -eu 

curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.aws.burrsutter.org/a/default
curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.azr.burrsutter.net/a/default
curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.gcp.burrsutter.dev/a/default

