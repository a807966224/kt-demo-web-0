package com.crash.course.ktdemoweb0.utils;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ClientResponse.Headers;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qnxy
 */
public class WebClientUtils {


    public static Function<ClientResponse, Mono<ClientResponse>> mutateResponse(Consumer<String> reqVal) {
        return resp -> resp.bodyToMono(String.class)
                .map(it -> {
                    reqVal.accept(it);
                    return resp.mutate().body(it).build();
                });
    }

    public static Function<ClientResponse, Mono<ClientResponse>> mutateResponse(BiFunction<String, Headers, Mono<String>> reqVal) {
        return resp -> resp.bodyToMono(String.class)
                .flatMap(it -> reqVal.apply(it, resp.headers()))
                .map(it -> resp.mutate().body(it).build());
    }
}
