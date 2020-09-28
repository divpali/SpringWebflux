package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime() {

        Flux<Long> timeFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(timeFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void testingWithVirtualTime() {

        VirtualTimeScheduler.getOrSet();   //this can be helpful in fasting the build process

        Flux<Long> timeFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> timeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
