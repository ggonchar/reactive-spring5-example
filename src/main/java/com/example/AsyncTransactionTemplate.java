package com.example;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Supplier;

@Component
public class AsyncTransactionTemplate {
    private final TransactionTemplate txTemplate;

    public AsyncTransactionTemplate(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }

    public <T> Mono<T> asyncTx(Supplier<T> s) {
        return Mono.fromCallable(() -> txTemplate.execute(status -> s.get()))
                .publishOn(Schedulers.elastic())
                .publishOn(Schedulers.parallel());
    }
}
