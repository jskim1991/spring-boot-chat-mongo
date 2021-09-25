package io.jay.springbootmongochat.order;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, String> {

    Flux<Order> findByProductId(String productId);
}
