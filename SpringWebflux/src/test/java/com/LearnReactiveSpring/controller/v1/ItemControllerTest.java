package com.LearnReactiveSpring.controller.v1;

import com.LearnReactiveSpring.model.Item;
import com.LearnReactiveSpring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.LearnReactiveSpring.constants.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> testData() {
        //setupData
        return Arrays.asList(new Item(null,"Samsung TV", 400.0),
                new Item(null, "LG TV", 500.0),
                new Item(null, "Apple watch", 100.0),
                new Item("ABC", "Beats headphone", 30.0));
    }

    @BeforeEach
    public void setup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(testData()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted item is : "+item);
                })
                .blockLast();  //when the clock call is there in any method then the setup method gets executed first completely

    }

    @Test
    public void getAllItemsTest() {

        webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }

    @Test
    public void getAllItemsTest_approach2() {

        webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response ->{
                   List<Item> itemList = response.getResponseBody();
                    itemList.forEach((item -> {
                        assertTrue(item.getId()!=null);
                    }));
                });

    }

    @Test
    public void getAllItemsTest_approach3() {

        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("value from network : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemByIdByTest() {

        webTestClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 30.0);

    }

    @Test
    public void getItemByIdBy_NoyFoundTest() {

        webTestClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"), "PQR")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItemTest() {

        Item item = new Item(null, "iPhone X", 999.99);

        webTestClient.post().uri(ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()   //actual connection happens at "exchange" and body gets posted
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("iPhone X")
                .jsonPath("$.price").isEqualTo(999.99);

    }

    @Test
    public void deleteItemTest() {
        webTestClient.delete().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemByIdTest() {

        double new_Price = 129.99;
        Item item = new Item(null, "Beats headphone", new_Price);

        webTestClient.put().uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", new_Price);

    }

    @Test
    public void updateItemByIdTest_NotFound() {

        double new_Price = 129.99;
        Item item = new Item(null, "Beats headphone", new_Price);

        webTestClient.put().uri(ITEM_END_POINT_V1.concat("/{id}"), "PQR")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void runtimrExceptionTest() {
        webTestClient.get().uri(ITEM_END_POINT_V1+"/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("RuntimeException occurred.");
    }

}
