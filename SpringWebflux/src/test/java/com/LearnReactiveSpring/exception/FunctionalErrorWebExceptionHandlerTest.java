package com.LearnReactiveSpring.exception;

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

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class FunctionalErrorWebExceptionHandlerTest {

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

    @Test
    public void runtimeExceptionTest() {
        webTestClient.get().uri("/fun/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message", "RuntimeException occurred.");
    }
}
