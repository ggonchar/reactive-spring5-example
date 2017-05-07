# Overview
This application demonstrates main concerns of reactive applications design with Spring 5 WebFlux & Reactor with different integrations such as WebClient, Spring Data MongoDB, Spring Data JPA.

# Run

MongoDB running on localhost:27017 required. Could be quickly launched with Docker:
```bash
docker run --name some-mongo -p 0.0.0.0:27017:27017 -d mongo
```

To run application with Maven and Spring Boot:
```bash
mvn spring-boot:run
```