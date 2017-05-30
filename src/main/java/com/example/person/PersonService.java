package com.example.person;

import com.example.AsyncTransactionTemplate;
import com.example.ip.IpService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PersonService {

    private final IpService ipService;

    private final PersonRepository repository;

    private final AsyncTransactionTemplate txTemplate;

    public PersonService(IpService ipService, PersonRepository repository, AsyncTransactionTemplate txTemplate) {
        this.ipService = ipService;
        this.repository = repository;
        this.txTemplate = txTemplate;
    }

    public Mono<Person> addPerson(Mono<Person> mono) {
        return mono.flatMap(this::addIpInfo)
                .flatMap(this::save);
    }

    private Mono<Person> save(Person person) {
        return txTemplate.asyncTx(() -> repository.save(person));
    }

    private Mono<Person> addIpInfo(Person person) {
        return ipService.getIpInfo(person.getIp()).map(ipInfo -> person.copyWithIpInfo(ipInfo));
    }

    public Mono<Person> findById(long id) {
        return txTemplate.asyncTx(() -> repository.findById(id))
                .flatMap(optional -> Mono.justOrEmpty(optional));
    }

    public Flux<Person> findAll() {
        return txTemplate.asyncTx(() -> repository.findAll()).flatMapIterable(v -> v);
    }


}
