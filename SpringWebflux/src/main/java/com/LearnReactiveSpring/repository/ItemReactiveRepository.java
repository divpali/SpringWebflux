package com.LearnReactiveSpring.repository;

import com.LearnReactiveSpring.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    Mono<Item> findByDescription(String description);
}
