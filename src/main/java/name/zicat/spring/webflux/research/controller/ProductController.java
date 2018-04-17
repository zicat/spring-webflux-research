package name.zicat.spring.webflux.research.controller;

import name.zicat.spring.webflux.research.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @RequestMapping(value = "/product", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ProductInfo> product(final Integer productId) {
        Mono<ProductInfo> result = Mono.fromCallable(() ->
            new ProductInfo(productId)
        ).subscribeOn(scheduler);
        return result;
    }
}
