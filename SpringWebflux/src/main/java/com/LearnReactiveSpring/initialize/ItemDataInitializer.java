package com.LearnReactiveSpring.initialize;

import com.LearnReactiveSpring.model.Item;
import com.LearnReactiveSpring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/*
    This classes is for adding data of the items entity in mongodb
 */
@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null,"Samsung TV", 400.0),
                             new Item(null, "LG TV", 500.0),
                             new Item(null, "Apple watch", 100.0),
                             new Item("ABC", "Beats headphone", 30.0));


    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from CommandLineRunner : "+item);
                });

    }
}
