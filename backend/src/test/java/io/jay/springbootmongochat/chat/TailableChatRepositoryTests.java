package io.jay.springbootmongochat.chat;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataMongoTest
public class TailableChatRepositoryTests {

    @Autowired
    private ReactiveMongoTemplate operations;

    @Autowired
    private ChatRepository repository;

    @BeforeEach
    void setup() {
        CollectionOptions capped = CollectionOptions.empty().size(1024 * 1024).maxDocuments(100).capped();

        Mono<MongoCollection<Document>> recreateCollection = operations.collectionExists(Chat.class)
                .flatMap(exists -> exists ? operations.dropCollection(Chat.class) : Mono.empty())
                .then(operations.createCollection(Chat.class, capped));

        StepVerifier.create(recreateCollection)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_findByRoomNumber_tails() throws InterruptedException {
        Queue<Chat> chats = new ConcurrentLinkedQueue<>();
        StepVerifier.create(this.insert(1).then(this.insert(1)))
                .expectNextCount(1)
                .verifyComplete();

        repository.findByChatId(1)
                .doOnNext(chats::add)
                .subscribe();

        TimeUnit.MILLISECONDS.sleep(50);
        assertThat(chats.size(), equalTo(2));

        StepVerifier.create(this.insert(1).then(this.insert(1)))
                .expectNextCount(1)
                .verifyComplete();

        TimeUnit.MILLISECONDS.sleep(50);
        assertThat(chats.size(), equalTo(4));
    }

    private Mono<Chat> insert(int roomNumber) {
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID().toString());
        chat.setSender("sender");
        chat.setReceiver("receiver");
        chat.setMessage("New message");
        chat.setCreatedAt(LocalDateTime.now());
        chat.setChatId(roomNumber);
        return repository.save(chat);
    }
}
