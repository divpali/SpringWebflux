package com.LearnReactiveSpring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient; //Non blocking  is required to write test case for non blocking endpoint

    @Test
    public void returnFluxTest_approach1() {
        Flux<Integer> strFlux = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()  //invokes endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(strFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void returnFluxTest_approach2() {

        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void returnFluxTest_approach3() {
        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expectedIntegerList,entityExchangeResult.getResponseBody());

    }

    @Test
    public void returnFluxTest_approach4() {
        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);

        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    assertEquals(expectedIntegerList,listEntityExchangeResult.getResponseBody());
                });
    }

    @Test
    public void returnFluxStreamTest_approach1() {

        Flux<Long> longStreamFlux = webTestClient.get()
                .uri("/fluxInfiniteStream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void retrunMonoTest() {
        Integer expectValue = new Integer(1);

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expectValue,response.getResponseBody());
                });
    }


}
