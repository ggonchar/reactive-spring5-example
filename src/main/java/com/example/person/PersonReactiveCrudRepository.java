package com.example.person;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PersonReactiveCrudRepository extends ReactiveCrudRepository<Person, String> {

}