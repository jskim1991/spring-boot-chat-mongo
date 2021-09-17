package io.jay.springbootmongochat;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

//    @Query("{sender: ?0, receiver: ?1}")
    @Tailable
    Flux<Chat> findBySenderAndReceiver(String sender, String receiver);

//    @Query("{roomNumber: ?0}")
    @Tailable
    Flux<Chat> findByRoomNumber(Integer roomNumber);
}
