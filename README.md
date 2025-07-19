# data-accessor-redis-web
This is a WEB API that works as a microservice within the Toposoid project.
Toposoid is a knowledge base construction platform.(see [Toposoid　Root Project](https://github.com/toposoid/toposoid.git))
This microservice get information from Redis in-memory database. outputs the result in JSON.

[![Test And Build](https://github.com/toposoid/data-accessor-redis-web/actions/workflows/action.yml/badge.svg)](https://github.com/toposoid/data-accessor-redis-web/actions/workflows/action.yml)

## Dependency in toposoid Project

## Requirements
* Docker version 20.10.x, or later
* docker-compose version 1.22.x

## Recommended Environment For Standalone
Required: at least 2GB of RAM
Required: at least 1.13GB of HDD(Docker Image Size)

## Setup For Standalone
```bssh
docker-compose up
```
The first startup takes a long time until docker pull finishes.
## Usage
```bash
#Set Data
curl -X POST -H "Content-Type: application/json" -H 'X_TOPOSOID_TRANSVERSAL_STATE: {"userId":"test-user", "username":"guest", "roleId":0, "csrfToken":""}' -d '{
  "user":"test-user", "key":"hoge", "value":"fuga"
}' http://localhost:9015/setUserData 

#Get Data
curl -X POST -H "Content-Type: application/json" -H 'X_TOPOSOID_TRANSVERSAL_STATE: {"userId":"test-user", "username":"guest", "roleId":0, "csrfToken":""}' -d '{
  "user":"test-user", "key":"hoge", "value":""
}' http://localhost:9015/getUserData 

```

## Note
* This microservice uses 9015 as the default port.
* If you want to run in a remote environment or a virtual environment, change PRIVATE_IP_ADDRESS in docker-compose.yml according to your environment.

## License
This program is offered under a commercial and under the AGPL license.
For commercial licensing, contact us at https://toposoid.com/contact.  For AGPL licensing, see below.

AGPL licensing:
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

## Author
* Makoto Kubodera([Linked Ideal LLC.](https://linked-ideal.com/))

Thank you!

