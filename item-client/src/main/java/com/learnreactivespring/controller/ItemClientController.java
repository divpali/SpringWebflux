package com.learnreactivespring.controller;

import com.learnreactivespring.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
This is the class going to behave as a client
 */
@RestController
@Slf4j
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    /*
        curl http://localhost:8081/client/retrieve
     */
    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in client project");
    }

    /*
        curl http://localhost:8081/client/exchange
     */
    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return webClient.get().uri("/v1/items")
                .exchange() //it give you access to client response
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client project exchange : ");

    }

    /*
        curl http://localhost:8081/client/retrieve/singleItem
     */
    @GetMapping("/client/retrieve/singleItem")
    public Mono<Item> getItemByIdUsingRetrieve() {
        String id = "ABC";
        return webClient.get().uri("/v1/items/{id}", id)
                .retrieve() //it give you access to client response
                .bodyToMono(Item.class)
                .log("Items in client project retrieve single item : ");

    }

    /*
        curl http://localhost:8081/client/exchange/singleItem
     */
    @GetMapping("/client/exchange/singleItem")
    public Mono<Item> getItemByIdUsingExchange() {
        String id = "ABC";
        return webClient.get().uri("/v1/items/{id}", id)
                .exchange() //it give you access to client response
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Items in client project exchange single item : ");

    }

    /*
        curl -d '{"id":null,"description":"Google Nest","price":250.0}' -H "Content-Type: application/json" -X POST http://localhost:8081/client/createItem
        -d -> provide data
        -H -> header
        -X -> hhtp method -> POST

     */
    @PostMapping("/client/createItem")
    public Mono<Item> createItem(@RequestBody Item item) {
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("created item is : ");
    }

    /*
        curl -d '{"id":null,"description":"Beats headphone","price":45.99}' -H "Content-Type: application/json" -X PUT http://localhost:8081/client/updateItem/ABC
     */
    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
        return webClient.put().uri("/v1/items/{id}", id)
                .body(Mono.just(item),Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("updated item is : ");
    }

    /*
        curl -X "DELETE" http://localhost:8081/client/deleteItem/ABC
     */
    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                .retrieve()   //going to invoke actual endpoint
                . bodyToMono(Void.class)
                .log("Deleted item is : ");

    }

    //mapping to connect to runtime error method
    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve() {
        return webClient.get()
                .uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap((errorMessage) -> {
                        log.error("The error message is : "+errorMessage);
                        throw new RuntimeException(errorMessage);
                    });
                })
                .bodyToFlux(Item.class);
    }

    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange() {
        return webClient.get()
                .uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("error message in error exchange : "+errorMessage);
                                    throw new RuntimeException(errorMessage);
                                });
                    } else {
                        return clientResponse.bodyToFlux(Item.class);
                    }

                });
    }


}
