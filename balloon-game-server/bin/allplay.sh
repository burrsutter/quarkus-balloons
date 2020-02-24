#!/bin/bash
set -eu 

curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.aws.burrsutter.org/a/play
curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.azr.burrsutter.net/a/play
curl -v -H "Authorization: Bearer $JWT_TOKEN" gameserver-game.apps.gcp.burrsutter.dev/a/play

