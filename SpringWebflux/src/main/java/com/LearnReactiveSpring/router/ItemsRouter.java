package com.LearnReactiveSpring.router;


import com.LearnReactiveSpring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.LearnReactiveSpring.constants.ItemConstants.ITEM_FUCNTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUCNTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::getAllItems)
                .andRoute(GET(ITEM_FUCNTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::getItemById)
                .andRoute(POST(ITEM_FUCNTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::createItem)
                .andRoute(POST(ITEM_FUCNTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::deleteItem)
                .andRoute(PUT(ITEM_FUCNTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::updateItemById);

    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET("/fun/runtimeException").and(accept(MediaType.APPLICATION_JSON))
                            ,itemsHandler::itemException);

    }

}
