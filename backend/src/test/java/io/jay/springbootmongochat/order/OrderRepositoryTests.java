package io.jay.springbootmongochat.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

@DataMongoTest
public class OrderRepositoryTests {

    private final Collection<Order> orders = Arrays.asList(
            new Order(UUID.randomUUID().toString(), "1"),
            new Order(UUID.randomUUID().toString(), "2"),
            new Order(UUID.randomUUID().toString(), "2"));
    private final Predicate<Order> predicate = order -> this.orders.stream()
            .filter(candidateOrder -> candidateOrder.getId().equalsIgnoreCase(order.getId()))
            .anyMatch(candidateOrder -> candidateOrder.getProductId().equalsIgnoreCase(order.getProductId()));
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        Flux<Order> saveAll = this.orderRepository.deleteAll()
                .thenMany(this.orderRepository.saveAll(this.orders));

        StepVerifier.create(saveAll)
                .expectNextMatches(this.predicate)
                .expectNextMatches(this.predicate)
                .expectNextMatches(this.predicate)
                .verifyComplete();
    }

    @Test
    void findAll() {
        StepVerifier.create(this.orderRepository.findAll())
                .expectNextMatches(this.predicate)
                .expectNextMatches(this.predicate)
                .expectNextMatches(this.predicate)
                .verifyComplete();
    }

    @Test
    void findByProductId() {
        StepVerifier.create(this.orderRepository.findByProductId("2"))
                .expectNextCount(2)
                .verifyComplete();
    }
}
