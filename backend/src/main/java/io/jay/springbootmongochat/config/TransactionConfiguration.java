package io.jay.springbootmongochat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {

    @Bean
    public TransactionalOperator transactionOperation(ReactiveTransactionManager txm) {
        return TransactionalOperator.create(txm);
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(ReactiveMongoDatabaseFactory rdf) {
        return new ReactiveMongoTransactionManager(rdf);
    }
}
