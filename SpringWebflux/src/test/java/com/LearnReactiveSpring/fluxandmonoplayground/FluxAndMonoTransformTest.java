package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam","ana","jack","jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM","ANA","JACK","JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,3,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Length_Repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(2)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,3,4,5,4,3,4,5,4,3,4,5)
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_Length_Filter() {

        //using sequence of operations combined together to build a pipeline
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    //if you have to call a DB or external service that returns a flux for each and every element
    @Test
    public void transformUsingFlatMap() {
        Flux<String> strNames = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); //A->List[A,newValue], B-> List[B,newValue]
                })
                .log();

        StepVerifier.create(strNames)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s,"newValue");
    }

    @Test
    public void transformUsingFlatMap_using_parallel() {
        Flux<String> strNames = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) //Flux<Flux<String>>  -> {A,B}, {C,D}, {E,F}
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel())
                )
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(strNames)
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    public void transformUsingFlatMap_using_parallel_maintain_order() {
        Flux<String> strNames = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)  //Flux<Flux<String>>  -> {A,B}, {C,D}, {E,F}
                /*.concatMap((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel())
                )*/
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel())
                )
                .flatMap(s -> Flux.fromIterable(s))
                .log();



        StepVerifier.create(strNames)
                .expectNextCount(12)
                .verifyComplete();
    }


}
