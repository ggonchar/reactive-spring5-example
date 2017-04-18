package com.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableReactiveMongoRepositories
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {

    private final String databaseName;

    public MongoConfiguration(@Value("${mongo.database.name}") String databaseName) {
        this.databaseName = databaseName;
    }

    @Bean
    ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

}