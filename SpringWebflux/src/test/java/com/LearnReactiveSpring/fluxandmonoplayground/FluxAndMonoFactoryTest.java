package com.LearnReactiveSpring.fluxandmonoplayground;

//    Flux & Mono - Exploring Factory Methods

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam","ana","jack","jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam","ana","jack","jenny")
                .verifyComplete();

    }

    @Test
    public void fluxFromArray() {
        String[] strName = new String[]{"adam","ana","jack","jenny"};

        Flux<String> namesFlux = Flux.fromArray(strName);

        StepVerifier.create(namesFlux)
                .expectNext("adam","ana","jack","jenny")
                .verifyComplete();

    }

    //Stream API - Java 8
    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("adam","ana","jack","jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> monoStr = Mono.justOrEmpty(null); //Mono.Empty();
        StepVerifier.create(monoStr.log())
                .verifyComplete();
    }

    //Supplier - functional interface
    @Test
    public void monoUsingSupplier() {
        Supplier<String> strSupplier = () -> "adam";

        Mono<String> strMono = Mono.fromSupplier(strSupplier);

        System.out.println(strSupplier.get());

        StepVerifier.create(strMono.log())
                .expectNext("adam")
                .verifyComplete();

    }

    //range
    @Test
    public void fluxUsingRange() {
        Flux<Integer> intFlux = Flux.range(1, 5).log();

        StepVerifier.create(intFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }


}
