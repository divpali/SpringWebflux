package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.core.Exceptions.*;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")); //this line does not get executed

        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectError(RuntimeException.class)
                .verify();

    }

    //Using OnErrorResume
    @Test
    public void fluxErrorHandling_OnErrorResume() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")) //this line does not get executed
                .onErrorResume(e -> {
                    System.out.println("Exception is : "+ e);
                    return Flux.just("default", "default1");
                });


        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
//                .expectError(RuntimeException.class)
                .expectNext("default")
                .expectNext("default1")
                .verifyComplete();

    }

    @Test
    public void fluxErrorHandling_OnErrorReturn() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")) //this line does not get executed
                .onErrorReturn("Return");


        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("Return")
                .verifyComplete();

    }

    //converting and exception from one type to another
    @Test
    public void fluxErrorHandling_OnErrorMap() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")) //this line does not get executed
                .onErrorMap(e -> new CustomException(e));


        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();

    }

    //after catching the exception and handling it, if we want to retry it
    @Test
    public void fluxErrorHandling_OnErrorMap_withRetry() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")) //this line does not get executed
                .onErrorMap(e -> new CustomException(e))
                .retry(2);

        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();

    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetryBackOff() {
        Flux<String> strFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(RuntimeException::new))
                .concatWith(Flux.just("Done")) //this line does not get executed
                .onErrorMap(e -> new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(5));  //giving 5 sec backoff before we perform retry

        StepVerifier.create(strFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(Exception.class)
                .verify();

    }
}
