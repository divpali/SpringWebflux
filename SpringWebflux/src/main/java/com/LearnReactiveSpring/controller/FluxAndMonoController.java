package com.LearnReactiveSpring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")   //when a browser hits this endpoint, it acts like a subscriber
    public Flux<Integer> returnFlux() {
        return Flux.just(1,2,3,4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    //browser acts like a subscriber to this flux
    //there is a delay of 1 sec for each element
    //reload in browser took 4 sec to display the elements
    //in the endpoint the type of return type is not mentioned and hence return as JSON return type
    //by default browser is a blocking client

    @GetMapping(value = "/fluxStream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)   //as the data is available its being rendered as stream
    public Flux<Integer> returnFluxStream() {
        return Flux.just(1,2,3,4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxInfiniteStream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnInfiniteFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1)
                .log();
    }


}
