package com.example;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreaker ipServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofMinutes(5))
                .ringBufferSizeInClosedState(100)
                .ringBufferSizeInHalfOpenState(30)
                .failureRateThreshold(10)
                .build();
        return CircuitBreaker.of("ipService", config);
    }

}
