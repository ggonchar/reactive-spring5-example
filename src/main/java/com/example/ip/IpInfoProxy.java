package com.example.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

interface IpInfoProxy {
    Mono<ClientResponse> requestIpInfo(String ip);
}

@Component
class PrimaryIpInfoProxy implements IpInfoProxy {
    private final static Logger log = LoggerFactory.getLogger(IpInfoProxy.class);

    private final String ipServiceUrl;

    PrimaryIpInfoProxy(@Value("${ip.service.url}") String ipServiceUrl) {
        this.ipServiceUrl = ipServiceUrl;
    }

    public Mono<ClientResponse> requestIpInfo(String ip) {
        return WebClient.create(ipServiceUrl).get().uri("/json/{ip}", ip).exchange().doOnError(t ->
                log.error("Failed to obtain IP information from primary provider", t)
        );
    }

}

@Component
class FallbackIpInfoProxy implements IpInfoProxy {
    private final static Logger log = LoggerFactory.getLogger(IpInfoProxy.class);

    private final String ipServiceUrl;

    FallbackIpInfoProxy(@Value("${ip.fallback.service.url}") String ipServiceUrl) {
        this.ipServiceUrl = ipServiceUrl;
    }

    public Mono<ClientResponse> requestIpInfo(String ip) {
        return WebClient.create(ipServiceUrl).get().uri("/json/{ip}", ip).exchange().doOnError(t ->
                log.error("Failed to obtain IP information from fallback provider", t)
        );
    }

}
