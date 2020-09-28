package com.LearnReactiveSpring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    /*
        how to have multiple subscriber and how the data will flow if you have multiple subsciber
     */
    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> strFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        //everytime a new subscriber gets assigned it emits the value from beginning
        //this is an example of cold publisher
        strFlux.subscribe(s -> System.out.println("Subscriber 1 : "+s));  //emits a value from beginning

        Thread.sleep(2000);

        strFlux.subscribe(s -> System.out.println("Subscriber 2 : "+s)); //emits a value from beginning

        Thread.sleep(4000);

    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> strFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = strFlux.publish();
        connectableFlux.connect();  //in order to make this publisher as a hot publisher we use connect()

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : "+s));

        Thread.sleep(2000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : "+s)); //does not emit values from beginning

        Thread.sleep(4000);
    }




}
