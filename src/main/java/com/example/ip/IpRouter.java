package com.example.ip;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class IpRouter {
    private final String ipServiceUrl;

    public IpRouter(@Value("${ip.service.url}") String ipServiceUrl) {
        this.ipServiceUrl = ipServiceUrl;
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
