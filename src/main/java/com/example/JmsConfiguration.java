package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Configuration
@EnableIntegration
public class JmsConfiguration {

    @Bean
    public Scheduler jmsProducerScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(10));
    }

}

