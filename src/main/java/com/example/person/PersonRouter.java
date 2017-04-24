package com.example.person;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PersonRouter {

    private final PersonRepository repository;

    private final PersonService service;

    private final PersonReciever reciever;

    public PersonRouter(PersonRepository repository, PersonService service, PersonReciever reciever) {
        this.repository = repository;
        this.service = service;
        this.reciever = reciever;
    }

    @Bean
    public RouterFunction<?> personRoutes() {
        return RouterFunctions
                .route(GET("/person/{id}").and(accept(APPLICATION_JSON)), request -> {
                    String personId = request.pathVariable("id");
                    Mono<ServerResponse> notFound = ServerResponse.notFound().build();
                    return repository.findOne(personId)
                            .flatMap(person -> ServerResponse.ok().body(Mono.just(person), Person.class))
                            .switchIfEmpty(notFound);
                })
                .andRoute(GET("/person").and(accept(APPLICATION_JSON)), request ->
                        ServerResponse.ok().body(repository.findAll(), Person.class))
                .andRoute(POST("/person").and(contentType(APPLICATION_JSON)), request ->
                        ServerResponse.ok().body(service.addPerson(request.bodyToMono(Person.class)), Person.class))
                .andRoute(POST("/person/update"), request -> {
                    service.updateIpInfo();
                    return ServerResponse.ok().build();
                })
                .andRoute(GET("/person/stream").and(accept(TEXT_EVENT_STREAM)), request ->
                     ServerResponse.ok().body(reciever.messageStream())
                );
    }


}
