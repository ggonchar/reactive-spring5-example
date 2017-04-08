package com.example;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PersonReactiveCrudRepository extends ReactiveCrudRepository<Person, String> {

}