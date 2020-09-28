package com.LearnReactiveSpring.repository;

import com.LearnReactiveSpring.model.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@DirtiesContext
public class ItemReactiveRepositoryTest {

    List<Item> itemList = Arrays.asList(new Item(null,"Samsung TV", 400.0),
                                        new Item(null, "LG TV", 500.0),
                                        new Item(null, "Apple watch", 100.0),
                                        new Item(null, "Beats headphone", 300.0),
                                        new Item("ABC", "Bose headphone", 300.0));

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setup() {
        itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(itemList))
        .flatMap(itemReactiveRepository::save)
        .doOnNext((item) -> {
            System.out.println("Inserted item is : "+item);
        })
        .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(5)
            .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose headphone"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose headphone").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item(null,"Google Home Mini", 30.00);

        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log())
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId()!=null && item1.getDescription().equals("Google Home Mini")))
                .verifyComplete();
    }

    @Test
    public void updateItem() {

        double newPrice = 25.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Beats headphone")
                .map(item -> {
                    item.setPrice(newPrice);   //setting new price  {map is used for conversion}
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));;  //saving the item with new price

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice()==25.00)
                .verifyComplete();
    }

    @Test
    public void deleteItemId() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)   //map is used to transform one type to another
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .flatMap((item) -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

}
