package com.example.person;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.jms.ConnectionFactory;

@Component
public class PersonReciever {

    private final ConnectionFactory connectionFactory;

    private final JmsTemplate jmsTemplate;

    private final String queueName;

    public PersonReciever(ConnectionFactory connectionFactory, JmsTemplate jmsTemplate, @Value("${jms.person.queue-name}") String queueName) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    public Flux<String> messageStream() {
        return Flux.from(jmsReactiveSource())
                .map(Message::getPayload);
    }

    private Publisher<Message<String>> jmsReactiveSource() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(this.connectionFactory)
                        .destination(queueName))
                .channel(MessageChannels.queue())
                .log(LoggingHandler.Level.DEBUG)
                .log()
                .toReactivePublisher();
    }

}
