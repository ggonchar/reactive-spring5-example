package com.example.ip;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Service
public class IpService {
    private final static Logger log = LoggerFactory.getLogger(IpService.class);

    public static final String EMPTY_IP_INFO = "{}";

    private final CircuitBreaker circuitBreaker;
    private final IpInfoProxy primaryProxy, fallbackProxy;

    public IpService(@Qualifier("primaryIpInfoProxy") IpInfoProxy primaryProxy,
                     @Qualifier("fallbackIpInfoProxy") IpInfoProxy fallbackProxy,
                     @Qualifier("ipServiceCircuitBreaker") CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
        this.primaryProxy = primaryProxy;
        this.fallbackProxy = fallbackProxy;
    }

    public Mono<String> getIpInfo(String ip) {
        return Try.of(CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () ->
                primaryProxy.requestIpInfo(ip).onErrorResume(t -> {
                    circuitBreaker.onError(0l, t);
                    return fallbackProxy.requestIpInfo(ip);
                })
        )).getOrElse(() -> fallbackProxy.requestIpInfo(ip))
                .flatMap(this::parse)
                .doOnError(t -> log.error("Failed to obtain or parse ip info", t))
                .onErrorReturn(EMPTY_IP_INFO);
    }


    private Mono<String> parse(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class);
        } else {
            throw new IllegalArgumentException("Request for ip info failed: " + response);
        }
    }

}

