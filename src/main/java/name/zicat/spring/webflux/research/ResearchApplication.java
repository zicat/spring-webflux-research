package name.zicat.spring.webflux.research;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.ipc.netty.resources.LoopResources;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lz31 on 2018/4/17.
 */
@SpringBootApplication
public class ResearchApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(ResearchApplication.class).run(args);
    }

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(builder -> {
            builder.loopResources(LoopResources.create("research-http", 2, true));
        });
        return factory;
    }

    @Bean
    public Scheduler scheduler() {
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        return Schedulers.fromExecutor(threadPool);
    }
}
