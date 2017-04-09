package com.example.address;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class AddressRouter {
    private final AddressRepository repository;

    public AddressRouter(AddressRepository repository) {
        this.repository = repository;
    }

    @Bean
    public RouterFunction<?> addressRoutes() {
        return RouterFunctions
                .route(GET("/address").and(accept(APPLICATION_JSON)), request -> {
                    Flux<Address> addresses = Mono.fromCallable(() -> repository.findAll())
                            .publishOn(Schedulers.elastic())
                            .flatMap(i -> Flux.fromIterable(i));
                    return ServerResponse.ok().body(addresses, Address.class);
                });
    }
}
