package com.example.person;

import com.example.ip.IpService;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PersonService {
    private final Logger log = LoggerFactory.getLogger(PersonRouter.class);

    private final IpService ipService;

    private final PersonRepository repository;

    private final PersonSender sender;

    public PersonService(IpService ipService, PersonRepository repository, PersonSender sender) {
        this.ipService = ipService;
        this.repository = repository;
        this.sender = sender;
    }

    public Mono<Person> addPerson(Mono<Person> mono) {
        return mono.flatMap(this::addIpInfo)
                .flatMap(repository::save)
                .flatMap(sender::send);
    }

    private Mono<Person> addIpInfo(Person person) {
        return ipService.getIpInfo(person.getIp()).map(ipInfo -> person.copyWithIpInfo(JSON.parse(ipInfo)));
    }

    public void updateIpInfo() {
        repository.findAll()
                .buffer(10)
                .onBackpressureBuffer(1000, BufferOverflowStrategy.ERROR)
                .retry(2)
                .parallel(2)
                .flatMap(this::updateIpInfo)
                .subscribe(
                        p -> log.info("Updated IP information for person with id {}", p.getId()),
                        t -> log.error("Failed IP information update stream", t)
                );
    }

    private Flux<Person> updateIpInfo(List<Person> batch) {
        return batch.stream()
                .map(p -> repository.save(addIpInfo(p)))
                .reduce(Flux.empty(), (Flux<Person> previous, Flux<Person> next) -> previous.concatWith(next));
    }
}
