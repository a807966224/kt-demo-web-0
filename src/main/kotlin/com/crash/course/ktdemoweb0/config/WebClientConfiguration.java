package com.crash.course.ktdemoweb0.config;

import com.crash.course.ktdemoweb0.utils.WebClientUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * @author Qnxy
 */
@Configuration
@RequiredArgsConstructor
@SpringBootConfiguration
@Slf4j
public class WebClientConfiguration {

    private final ObjectMapper objectMapper;

    @Bean("webClientBuilder")
    public WebClient.Builder webClientBuilder() {
        var httpClient = HttpClient.create()
                .wiretap(true)
                .responseTimeout(Duration.ofSeconds(1))
                .doOnConnect(conn -> {
                    log.info("connecting to: {}", conn.responseTimeout());
                });


        return WebClient.builder()
                .codecs(setCodecs())
                .filter(this::filter)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                ;
    }

    private Consumer<ClientCodecConfigurer> setCodecs() {
        return configurer -> {
            val clientDefaultCodecs = configurer.defaultCodecs();
            clientDefaultCodecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            clientDefaultCodecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
        };
    }


    private Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(ctxRequestTime -> {
            val requestTime = Instant.now();
            val newRequest = ClientRequest.from(request)
                    .body((r, ctx) -> request.body().insert(new MyClientHttpRequestDecorator(r), ctx))
                    .build();

            return next.exchange(newRequest)
                    .flatMap(WebClientUtils.mutateResponse(respV -> log.debug("""
                                    merchant call response: [{}]
                                        body: {}
                                        request consumes time: {}
                                    """,
                            request.url(),
                            respV,
                            Duration.between(requestTime, Instant.now())
                    )));

        });

    }

    private static class MyClientHttpRequestDecorator extends ClientHttpRequestDecorator {
        public MyClientHttpRequestDecorator(ClientHttpRequest r) {
            super(r);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            val dataBufferMono = DataBufferUtils.join(body)
                    .doOnNext(dataBuffer -> {
                        val reqVal = dataBuffer.toString(StandardCharsets.UTF_8);

                        log.debug("""
                                        merchant call request: {}
                                            body: {}
                                        """,
                                getURI(),
                                reqVal
                        );
                    });

            return super.writeWith(dataBufferMono);
        }
    }
}