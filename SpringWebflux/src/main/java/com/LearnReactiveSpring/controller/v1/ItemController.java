package com.LearnReactiveSpring.controller.v1;

import com.LearnReactiveSpring.model.Item;
import com.LearnReactiveSpring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.LearnReactiveSpring.constants.ItemConstants.ITEM_END_POINT_V1;


@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    //return a single item
    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity> getItemById(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    //create an item in the mongo db
    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    //delete an item
    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItemById(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    // update an item -
    // id and item is to be updated in the request = path variable and request body
    // using the id get the item from Mongo DB
    // update item retrieved with the value from request body
    // save the item
    // return the saved item
    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItemById(@PathVariable String id, @RequestBody Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    //Handling exception using @ExceptionHandler
    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException occurred.")));
    }

    /*@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Exception caught in handleRuntimeException : {} "+ ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }*/

}
