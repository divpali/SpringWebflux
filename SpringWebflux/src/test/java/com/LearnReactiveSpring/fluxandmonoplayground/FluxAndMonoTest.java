package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> strFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
//                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("After Error"))  //once error is emitted from flux it wont send any more data again
                .log();

        strFlux
                .subscribe(System.out::println,    //when you subscribe then the subscriber reads from flux
                        (e) -> System.err.println("Exception is "+e),
                        () -> System.out.println("Completed")); //this third parameter could be used if you want to explicitly says "Done"

        //request(unbounded) refers to Long.MAXVALUE

    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> strFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
                .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring", "Spring boot", "Reactive Spring") //we get the elements in the same order after subscribing
                .verifyComplete();   //verifyComplete ~ subscribe which allows flow of elements
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> strFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring", "Spring boot", "Reactive Spring") //we get the elements in the same order after subscribing
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred") //verify error meaage in line 38
                .verify();   //verify ~ subscribe which allows flow of elements in flux
    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> strFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(strFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();   //verify ~ subscribe which allows flow of elements in flux
    }

    @Test
    public void monoTest() {
        Mono<String> strMono = Mono.just("Spring")
                .log();

        Object res = strMono.subscribe(System.out::println);

        System.out.println("Divya1 : "+res.toString());


        StepVerifier.create(strMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error() {
        StepVerifier.create(Mono.error(RuntimeException::new).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
