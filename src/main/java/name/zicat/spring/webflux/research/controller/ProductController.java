package name.zicat.spring.webflux.research.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * Created by lz31 on 2018/4/17.
 */
@RestController
public class ProductController {

    @Autowired private Scheduler scheduler;

    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public Mono<String> product(final Integer productId) {
        Mono<String> result = Mono.fromCallable(() ->
            "{\"productId\": \"" + productId + "\"}"
        ).subscribeOn(scheduler);
        return result;
    }
}
