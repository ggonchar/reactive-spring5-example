package com.example.ip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class IpRouter {

    private final IpService service;

    public IpRouter(IpService service) {
        this.service = service;
    }

    @Bean
    public RouterFunction<?> ipRoutes() {
        return RouterFunctions.route(GET("/ip/{ip}").and(accept(APPLICATION_JSON)), request -> {
            final String ip = request.pathVariable("ip");
            Mono<String> response = service.getIpInfo(ip);
            return ServerResponse.ok().body(response, String.class);
        });
    }
}
