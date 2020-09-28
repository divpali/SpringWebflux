package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log(); //starts from 0 --> .....

        infiniteFlux.subscribe( ele -> {
            System.out.println("Value is : "+ele);
        });

        //in order to understand that the code above where we subcribe to flux is non blocking and asynchronous
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void infiniteSequenceTest() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() {  //map helps convert one form to another
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_WithDelay() {  //map helps convert one form to another
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
