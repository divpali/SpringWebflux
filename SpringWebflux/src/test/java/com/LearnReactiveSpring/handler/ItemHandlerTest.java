package com.LearnReactiveSpring.handler;

import com.LearnReactiveSpring.model.Item;
import com.LearnReactiveSpring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static com.LearnReactiveSpring.constants.ItemConstants.ITEM_FUCNTIONAL_END_POINT_V1;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> testData() {
        //setupData
        return Arrays.asList(new Item(null,"Samsung TV",400.0),
                             new Item(null, "LG TV",500.0),
                             new Item(null,"Apple watch",100.0),
                             new Item("ABC","Beats headphone",30.0));
    }

    @BeforeEach
    public void setup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(testData()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted item is : "+item);
                })
                .blockLast();  //when the block call is there in any method then the setup method gets executed first completely

    }

//    @Test
//    public void getAllItemsTest() {
//
//        webTestClient.get().uri(ITEM_FUCNTIONAL_END_POINT_V1)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBodyList(Item.class)
//                .hasSize(4);
//
//    }
//
//    @Test
//    public void getItemByIdByTest() {
//
//        webTestClient.get().uri(ITEM_FUCNTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.price", 30.0);
//
//    }

    @Test
    public void getItemByIdBy_NotFoundTest() {

        webTestClient.get().uri(ITEM_FUCNTIONAL_END_POINT_V1.concat("/{id}"), "PQR")
                .exchange()
                .expectStatus().isNotFound();

    }
}
