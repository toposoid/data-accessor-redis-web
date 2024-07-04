# data-accessor-redis-web
This is a WEB API that works as a microservice within the Toposoid project.
Toposoid is a knowledge base construction platform.(see [Toposoid　Root Project](https://github.com/toposoid/toposoid.git))
This microservice get information from Redis in-memory database. outputs the result in JSON.


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
curl -X POST -H "Content-Type: application/json" -d '{
  "user":"test-user", "key":"hoge", "value":"fuga"
}' http://localhost:9015/setUserData 

#Get Data
curl -X POST -H "Content-Type: application/json" -d '{
  "user":"test-user", "key":"hoge", "value":""
}' http://localhost:9015/getUserData 

```

## Note
* This microservice uses 9015 as the default port.
* If you want to run in a remote environment or a virtual environment, change PRIVATE_IP_ADDRESS in docker-compose.yml according to your environment.

## License
toposoid/data-accessor-redis-web is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).

## Author
* Makoto Kubodera([Linked Ideal LLC.](https://linked-ideal.com/))

Thank you!

