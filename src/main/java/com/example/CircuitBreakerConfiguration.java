package com.example;

import io.github.robwin.circuitbreaker.CircuitBreaker;
import io.github.robwin.circuitbreaker.CircuitBreakerConfig;
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
                .ringBufferSizeInHalfOpenState(100)
                .failureRateThreshold(10)
                .build();
        return CircuitBreaker.of("ipService", config);
    }

}
