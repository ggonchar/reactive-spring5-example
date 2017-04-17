package com.example.person;

import com.example.ip.IpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PersonRouter {

    private final PersonRepository repository;

    private final IpService ipService;

    public PersonRouter(PersonRepository repository, IpService ipService) {
        this.repository = repository;
        this.ipService = ipService;
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
                        ServerResponse.ok().body(addPerson(request.bodyToMono(Person.class)), Person.class));
    }

    private Mono<Person> addPerson(Mono<Person> mono) {
        return mono.flatMap(this::copyWithIpInfo).flatMap(repository::save);
    }

    private Mono<Person> copyWithIpInfo(Person person) {
        return ipService.getIpInfo(person.getIp()).map(ipInfo -> person.copyWithIpInfo(ipInfo));
    }

}
