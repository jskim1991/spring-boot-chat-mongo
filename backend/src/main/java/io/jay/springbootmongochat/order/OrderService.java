package io.jay.springbootmongochat.order;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ReactiveMongoTemplate template;
    private final TransactionalOperator operator;

    @Transactional
    public Flux<Order> createOrders(String... productIds) {
        return buildOrderFlux(template::insert, productIds);
    }

    public Flux<Order> createOrdersUsingTxOperator(String... productIds) {
        return this.operator.execute(status -> buildOrderFlux(template::insert, productIds));
    }

    private Flux<Order> buildOrderFlux(Function<Order, Mono<Order>> callback, String... productIds) {
        return Flux.just(productIds)
                .map(pid -> {
                    Assert.notNull(pid, "the product ID should not be null");
                    return pid;
                })
                .map(pid -> new Order(null, pid))
                .flatMap(callback);
    }
}
