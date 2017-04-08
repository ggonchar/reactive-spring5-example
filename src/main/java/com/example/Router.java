package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class Router {

    private final String ipServiceUrl;

    private final PersonReactiveCrudRepository repository;

    public Router(PersonReactiveCrudRepository repository, @Value("${ip.service.url}") String ipServiceUrl) {
        this.repository = repository;
        this.ipServiceUrl = ipServiceUrl;
    }

    @Bean
    public RouterFunction<?> personRoutes() {
        return RouterFunctions
                .route(GET("/person/{id}").and(accept(APPLICATION_JSON)), request -> {
                    String personId = request.pathVariable("id");
                    Mono<ServerResponse> notFound = ServerResponse.notFound().build();
                    return repository.findOne(personId)
                            .then(person -> ServerResponse.ok().body(Mono.just(person), Person.class))
                            .otherwiseIfEmpty(notFound);
                })
                .andRoute(GET("/person").and(accept(APPLICATION_JSON)), request ->
                        ServerResponse.ok().body(repository.findAll(), Person.class))
                .andRoute(POST("/person").and(contentType(APPLICATION_JSON)), request ->
                        ServerResponse.ok().body(repository.save(request.bodyToMono(Person.class)), Person.class));
    }

    @Bean
    public RouterFunction<?> ipRoutes() {
        return RouterFunctions.route(GET("/ip/{ip}").and(accept(APPLICATION_JSON)), request -> {
            String ip = request.pathVariable("ip");
            Mono<String> response = WebClient.create(ipServiceUrl).get().uri("/json/{ip}", ip)
                    .exchange().then(ipInfo -> ipInfo.bodyToMono(String.class));
            return ServerResponse.ok().body(response, String.class);
        });
    }

}
