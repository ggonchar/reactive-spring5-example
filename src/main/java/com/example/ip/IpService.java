package com.example.ip;

import io.github.robwin.circuitbreaker.CircuitBreaker;
import javaslang.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class IpService {
    private final String ipServiceUrl, ipFallbackServiceUrl;
    private final CircuitBreaker circuitBreaker;

    public IpService(@Value("${ip.service.url}") String ipServiceUrl, @Value("${ip.fallback.service.url}") String ipFallbackServiceUrl) {
        this.ipServiceUrl = ipServiceUrl;
        this.ipFallbackServiceUrl = ipFallbackServiceUrl;
        this.circuitBreaker = CircuitBreaker.ofDefaults("ipService");
    }

    public Mono<String> getIpInfo(String ip) {
        return getIpInfoResponse(ip)
                .onErrorResume(throwable -> {
                    circuitBreaker.onError(Duration.ZERO, throwable);
                    return getFallbackIpInfoResponse(ip);
                })
                .flatMap(ipInfo -> tryWithCircuitBreaker(() -> toMonoString(ipInfo)).getOrElse(getIpInfoFallback(ip)));
    }

    private <T> Try<T> tryWithCircuitBreaker(Try.CheckedSupplier<T> supplier) {
        return Try.of(CircuitBreaker.decorateCheckedSupplier(circuitBreaker, supplier));
    }

    private Mono<ClientResponse> getIpInfoResponse(String ip) {
        return WebClient.create(ipServiceUrl).get().uri("/json/{ip}", ip).exchange();
    }

    private Mono<ClientResponse> getFallbackIpInfoResponse(String ip) {
        return WebClient.create(ipFallbackServiceUrl).get().uri("/json/{ip}", ip).exchange();
    }

    private Mono<String> getIpInfoFallback(String ip) {
        return getFallbackIpInfoResponse(ip)
                .flatMap(ipInfo -> ipInfo.bodyToMono(String.class));
    }

    private Mono<String> toMonoString(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class);
        } else {
            throw new RuntimeException();
        }
    }
}
