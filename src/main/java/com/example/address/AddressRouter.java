package com.example.address;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class AddressRouter {

    private final AddressRepository repository;

    private final Scheduler scheduler;

    public AddressRouter(AddressRepository repository, @Qualifier("jdbcScheduler") Scheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    @Bean
    public RouterFunction<?> addressRoutes() {
        return RouterFunctions
                .route(GET("/address").and(accept(APPLICATION_JSON)), request -> {
                    Flux<Address> addresses = async(() -> repository.findAll())
                            .flatMapMany(Flux::fromIterable);
                    return ServerResponse.ok().body(addresses, Address.class);
                });
    }

    private <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable).publishOn(scheduler);
    }
}
