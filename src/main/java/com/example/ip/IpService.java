package com.example.ip;

import io.github.robwin.circuitbreaker.CircuitBreaker;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class IpService {
    private final static Logger log = LoggerFactory.getLogger(IpService.class);

    private static final String EMPTY_IP_INFO = "{}";

    private final String ipServiceUrl, ipFallbackServiceUrl;
    private final CircuitBreaker circuitBreaker;

    public IpService(@Value("${ip.service.url}") String ipServiceUrl,
                     @Value("${ip.fallback.service.url}") String ipFallbackServiceUrl,
                     @Qualifier("ipServiceCircuitBreaker") CircuitBreaker circuitBreaker) {
        this.ipServiceUrl = ipServiceUrl;
        this.ipFallbackServiceUrl = ipFallbackServiceUrl;
        this.circuitBreaker = circuitBreaker;
    }

    public Mono<String> getIpInfo(String ip) {
        return Try.of(CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () ->
                        requestIpInfo(ip).onErrorResume(t -> requestFallbackIpInfo(ip)).flatMap(this::parse)))
                .onFailure(t -> log.error("Failed to obtain IP information, fallback provider will be used", t))
                .getOrElse(Try.of(() -> requestFallbackIpInfo(ip).flatMap(this::parse))
                .onFailure(t -> log.error("Failed to obtain IP information from fallback provider, empty result will be returned", t))
                .getOrElse(Mono.just(EMPTY_IP_INFO)));
    }

    private Mono<String> parse(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class);
        } else {
            throw new IllegalArgumentException("Request for ip info failed: " + response);
        }
    }

    private Mono<ClientResponse> requestIpInfo(String ip) {
        return WebClient.create(ipServiceUrl).get().uri("/json/{ip}", ip).exchange();
    }

    private Mono<ClientResponse> requestFallbackIpInfo(String ip) {
        return WebClient.create(ipFallbackServiceUrl).get().uri("/json/{ip}", ip).exchange();
    }

}
