package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {


/*
                  -------- getAllItems() -------->
                  ---------- subscribe ---------->
                  -------- subscription --------->
      Subscriber  --------- request(1)  --------->  Publisher
    (Application) -------- onNext(item1) -------->  (DB/externalservice)
                  --------- request(1) ---------->
                  -------- onNext(item2) -------->
                  ---------- cancel() ----------->

*/

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1,10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)  //send me 1 element
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();

    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(ele -> System.out.println("Element is : " + ele),
                (e) -> System.err.println("Exception is : " + e), //exception
                () -> System.out.println("Done"), //completion event
                (subscription -> subscription.request(2))); //get access to subscription
    }

    @Test
    public void backPressure_cancel() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(ele -> System.out.println("Element is : " + ele),
                (e) -> System.err.println("Exception is : " + e), //exception
                () -> System.out.println("Done"), //completion event
                (subscription -> subscription.cancel()));

    }

    //how to have more control on data flow
    @Test
    public void customisedBackPressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {   //performing data level validation
                request(1);
                System.out.println("Value recieved is : "+ value);
                if(value == 4) {
                    cancel();
                }
            }
        });

    }

}
