package io.jay.springbootmongochat.order;

import io.jay.springbootmongochat.config.TransactionConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@Import({TransactionConfiguration.class, OrderService.class})
public class OrderServiceTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReactiveMongoTemplate template;

    @BeforeEach
    void setup() {
        Mono<Boolean> createIfMissing = template.collectionExists(Order.class)
                .filter(x -> !x)
                .flatMap(exists -> template.createCollection(Order.class))
                .thenReturn(true);

        StepVerifier.create(createIfMissing)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void createOrders() {
        Publisher<Order> orders = this.orderRepository.deleteAll()
                .thenMany(this.orderService.createOrders("1", "2", "3"))
                .thenMany(this.orderRepository.findAll());

        StepVerifier.create(orders)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void txRollback_usingTransactional() {
        Publisher<Order> orders = this.orderRepository.deleteAll()
                .thenMany(this.orderService.createOrders("1", "2", null))
                .thenMany(this.orderRepository.findAll());

        StepVerifier.create(orders)
                .expectNextCount(0)
                .verifyError();

        StepVerifier.create(this.orderRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void txRollback_usingTxOperator() {
        Publisher<Order> orders = this.orderRepository.deleteAll()
                .thenMany(this.orderService.createOrdersUsingTxOperator("1", "2", null))
                .thenMany(this.orderRepository.findAll());

        StepVerifier.create(orders)
                .expectNextCount(0)
                .verifyError();

        StepVerifier.create(this.orderRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }
}
