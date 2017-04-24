package com.example.person;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class PersonSender {

    private final JmsTemplate jmsTemplate;

    private final Scheduler jmsProducerScheduler;

    private final String queueName;

    public PersonSender(@Value("${jms.person.queue-name}") String queueName, JmsTemplate jmsTemplate, @Qualifier("jmsProducerScheduler") Scheduler jmsProducerScheduler) {
        this.queueName = queueName;
        this.jmsTemplate = jmsTemplate;
        this.jmsProducerScheduler = jmsProducerScheduler;
    }

    public Mono<Person> send(Person p) {
        return Mono.fromCallable(() -> {
            jmsTemplate.convertAndSend(queueName, p.getId());
            return p;
        }).publishOn(jmsProducerScheduler);
    }
}
