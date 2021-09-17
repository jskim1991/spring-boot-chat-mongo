package io.jay.springbootmongochat;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
public class ChatController {

    private final ChatRepository chatRepository;

    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMessageBySenderAndReceiver(@PathVariable String sender, @PathVariable String receiver) {
        return chatRepository.findBySenderAndReceiver(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/chat/roomNumber/{roomNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMessageByRoomNumber(@PathVariable Integer roomNumber) {
        return chatRepository.findByRoomNumber(roomNumber)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/chat")
    public Mono<Chat> setMessage(@RequestBody Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }
}
