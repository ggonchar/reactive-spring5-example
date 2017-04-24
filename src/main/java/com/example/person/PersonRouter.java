package com.example.person;

import com.example.ip.IpService;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PersonRouter {
    private final Logger log = LoggerFactory.getLogger(PersonRouter.class);

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
                        ServerResponse.ok().body(addPerson(request.bodyToMono(Person.class)), Person.class))
                .andRoute(GET("/person/update"), request -> {
                        updateIpInfo();
                        return ServerResponse.ok().build();
                });
    }

    private Mono<Person> addPerson(Mono<Person> mono) {
        return mono.flatMap(this::addIpInfo).flatMap(repository::save);
    }

    private Mono<Person> addIpInfo(Person person) {
        return ipService.getIpInfo(person.getIp()).map(ipInfo -> person.copyWithIpInfo(JSON.parse(ipInfo)));
    }

    private void updateIpInfo() {
        repository.findAll()
                .buffer(10)
                .onBackpressureBuffer(1000, BufferOverflowStrategy.ERROR)
                .retry(2)
                .parallel(2)
                .flatMap(this::updateIpInfo)
                .subscribe(
                        p -> log.trace("Updated IP information for person", p),
                        t -> log.error("Failed IP information update stream", t)
                );
    }

    private Flux<Person> updateIpInfo(List<Person> batch) {
        return batch.stream()
                .map(p -> repository.save(addIpInfo(p)))
                .reduce(Flux.empty(), (Flux<Person> previous, Flux<Person> next) -> previous.concatWith(next));
    }

}
