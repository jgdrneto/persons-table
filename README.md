## Technical Task - Persons report

The Persons report application is a project that consists of visualizing and calculating person salaries.

This project is built on docker using docker compose. It has a four containers:

- db: MySQL 8.0 database.
- db-client: PhpMyAdmin 5.2.0 web database client.
- backend: Rest API using Springboot 3.2.0 to manipulate database data.
- frontend: Web application using springboot, JSF and Primefaces 12.0.0

### Content

- [Prerequisites](#prerequisites)
- [How to build and run?](#how-to-build-and-run)
- [API documentation (Swagger)](#api-documentation-swagger)

## Prerequisites

- Docker (>= 26.0.0)
- Docker Compose (>= v2.25.0)

## How to build and run?

To build and run the application using Docker compose command:

```bash
$ docker compose up -d --build
```

The enviroments variables are located in the .env file

## API documentation (Swagger)

The backend API documentation can be viewed at Swagger UI ([http://localhost:8889/swagger#/](http://localhost:8889/swagger#/)). Swagger UI has a HTTP client that it allows testing on the avaliable endpoints, making requests and getting the responses.

## Frontend application

The frontend application can be viewed in ([http://localhost:9999](http://localhost:9999))