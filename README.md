## What is WebFlux framework
WebFlux framework是Spring5第一代响应式编程框架。
[官方文档](https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html)

<img src="/docs/image2018-4-14 14_50_43.png" />

架构上spring-webflux与spring-webmvc同级，是spring-webmvc的替代方案，底层网络模型脱离Servlet Api，采用了基于NIO的网络编程框架，支持包括Tomcat，Jetty，Netty等。

spring-webflux依然沿用了与spring-webmvc相同的Controller注解和路由方式，对于旧项目迁移至新项目中带来了便利。

中间层的业务代码由Reactive Stream方式管理，Reactive Streams默认采用Reactor框架，同时还支持另一款相对庞大的Reactive Stream框架RxJava。

可以说WebFlux框架是非常灵活的，选择WebFlux作为响应式异步网络编程是明智的选择。

## WebFlux framework搭建

1. $ check out this project
2. $ cd ${project root dir}
3. $ mvn clean install
4. $ mvn spring-boot:run
5. [open browser](http://localhost:8080/product?productId=10)

## Spring MVC差异对比

Spring MVC依然还是沿用Servlet编程模式，Servlet编程模式屏蔽了底层IO模型，所以很多Servlet容器都支持NIO和BIO等多种模式可选，但是Servlet对于线程模型的控制力度很粗。

Servlet网络编程采用的是线程或者线程池模型。在BIO模型下，通过accept模式阻塞，等待客户端请求，当接收到客户端请求后分配一个线程给一个request并采用回调的方式让用户实现servlet.service()方法，即用户在实现service()方法时是在一个分配好的线程中，而不是在accept线程中，accept线程就是所谓的Loop线程。同样在NIO Selector模型下，等待客户端的读写事件，然后为每个事件绑定线程并采用回调的方式让用户实现servlet.service()方法。这种模型的好处是request线程和loop线程进行了有效的隔离，即便是业务代码阻塞也完全不影响loop线程的运行，坏处是线程利用率低下，并发request数越大需要的线程越多，极大的影响了服务的极限性能。举个例子，假设有个业务非常简单，只返回系统当前时间，而系统当前时间通过一个变量维护，通过后台线程不断更新数据，request只简单的返回该变量，这种情况下完全不需要开启request级别线程，在loop线程中就可以直接处理request，只需要1个或几个线程就能应对极大并发的请求。这种业务场景下旧模型没有优势。

WebFlux模式的优势不是在于底层是否采用了NIO还是BIO，而是在上层替换了旧的Servlet线程模型。既然旧模型的问题在于用户无法使用Loop线程，所以WebFlux直接将Controller移交到Loop线程中，所以在Controller层返回的对象必须用Mono<T>或者Flux<T>包裹。这样做的好处在于允许用户在Loop线程中进行一些快速的非阻塞的操作，比如定义响应式编程模型对象等，不阻塞Loop线程，并绑定Scheduler，保证响应式编程模型能在新的线程中执行，提高并发性能。

将项目以Debug方式启动后，可以将断点设置在Controller入口的第一行代码，并用多个浏览器同时请求可以发现和Spring MVC的差异，当WebFlux设置Loop线程=1时，只有第一个request能进入Controller层，其他线程会在第一个request运行完所有Controller代码后才能依次被调用。而Spring MVC可以允许多个request并发调用Controller层代码。

## 性能测试
将WebFlux，VertX和Servlet(JAXRS2.1 Tomcat)三者进行性能测试。

### 测试场景
    1. 1000/user, 总request数量500000
    2. request：/product?productID=10 response：{"productId":"10"}
    3. service server 参数：
        cpu = 32  Intel(R) Xeon(R) CPU E5-2690 0 @ 2.90GHz,
        memory = 252GB
        linux = version 2.6.32-358.el6.x86_64
        java = Java(TM) SE Runtime Environment (build 1.8.0_73-b02)
        JVM = Java HotSpot(TM) 64-Bit Server VM (build 25.73-b02, mixed mode)
    4. client server 参数：same with service server
    5. 客户端测试工具 Apache Jmeter 4.0
    注：为了尽量和实际service保持一致，每个request耗时控制在20MS。

### 测试结果

VertX   summary = 2574404 in 00:00:57 = 45311.3/s Avg:  21 Min:  5 Max:  1133 Err:  75 (0.00%)

WebFlux summary = 2499748 in 00:00:57 = 44004.2/s Avg:  22 Min:  4 Max:  4020 Err: 65 (0.00%)

Tomcat  summary = 1831730 in 00:01:01 = 30234.1/s Avg:  27 Min:  3 Max: 31020 Err:  72 (0.00%)


### 结论

VertX  吞吐量45311.3/s排名第一，在1000并发平均响应性能损失5%，最坏情况性能损失5665%。

WebFlux吞吐量44004.2/s排名第二，在1000并发平均响应性能损失10%，最坏情况性能损失20100%。

Tomcat 吞吐量30234.1/s排名第三，在1000并发平均响应性能损失35%，最坏情况性能损失155100%。




### 总结
VertX的性能非常优秀，性能损失极低，最坏情况令人满意。

Spring WebFlux性能紧随其后，性能损失和最坏情况都稍逊于VertX。

Tomcat性能损失偏大，最坏情况性能损失不能令人满意。

### 试用场景
VertX的MVC还停留在API级别，在功能上比Spring MVC要弱，VertX适合一些历史包袱轻的新项目特别是作为Rest Api Service比较适合。

Spring Webflux由于支持Controller，对于一些已有的Spring MVC Service来说，迁移的工作量和风险相对较小。

对于一些service由于并发高导致部署过多instance的可以考虑切换至响应式Java Web，可以大大减少instance的部署，降低机器成不。对于并发不高的service虽然不能有明显的提升，但依然可以考虑切换，将这些service可以集中部署在几台server上，提高server资源的利用率和维护成本。

特别注意：不要简单的将WebFlux、VertX和性能挂上等号，每个服务的性能有诸多因素决定，对于性能优化还是要具体问题具体分析，不要盲目崇拜。

