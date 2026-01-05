package com.crash.course.ktdemoweb0.config.api

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.formatHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient
import reactor.core.publisher.Mono

/**
 * @Author: xin yi
 * @Date 2026/1/5 14:51
 * @Version 1.0
 */
@SpringBootConfiguration
class ExternalApiConfig(private val webClientBuilder: WebClient.Builder) {

//    @Value("\${base.url:http://kt-web-nacos-service-22226:22226}")
    @Value("\${base.url:http://example-apisix-1:9080}")
    private lateinit var baseUrl: String

    @Value("\${server.port:22223}")
    private var port: Int = 0


    @PostConstruct
    fun initialize() {
        log.info("externalApi init, baseUrl: $baseUrl, port: $port")
    }

    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(ExternalApiConfig::class.java)
        private val SENSITIVE_HEADERS = setOf(
            "authorization", "Authorization", "AUTHORIZATION",
            "cookie", "Cookie", "COOKIE",
            "x-api-key", "X-API-KEY", "X-Api-Key",
            "x-auth-token", "X-AUTH-TOKEN", "X-Auth-Token",
            "secret", "Secret", "SECRET"
        )
    }

    @Bean
    fun httpServiceProxyFactory(@Qualifier("webClient") webClient : WebClient): HttpServiceProxyFactory {
        log.info("externalApi init")
        return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
            .build()
    }


    @Bean
    fun webClient(): WebClient {
        return webClientBuilder.clone()
//            .baseUrl("$baseUrl:$port")
            .baseUrl(baseUrl)
            .filter(this.createLogFilter())
            .build()
    }


    @Bean
    fun httpTestApi(@Qualifier("httpServiceProxyFactory") httpServiceProxyFactory: HttpServiceProxyFactory): HttpTestApi {
        return httpServiceProxyFactory.createClient<HttpTestApi>()
    }


    private fun createLogFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            if (clientRequest != null) {
                try {
                    val safeHeaders = filterSensitiveHeaders(clientRequest.headers())
                    log.info("Request: ${clientRequest.method()} ${clientRequest.url()} ${formatHeaders(safeHeaders)}")
                } catch (e: Exception) {
                    log.warn("Error logging request: ${e.message}")
                }
            }
            Mono.just(clientRequest ?: ClientRequest.from(clientRequest).build())
        }
            .andThen(ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
                if (clientResponse != null) {
                    try {
                        log.info("Response: ${clientResponse.statusCode()}")
                    } catch (e: Exception) {
                        log.warn("Error logging response: ${e.message}")
                    }
                }
                Mono.just(clientResponse ?: return@ofResponseProcessor Mono.empty())
            })
    }

    private fun filterSensitiveHeaders(headers: HttpHeaders): HttpHeaders {
        val filteredHeaders = HttpHeaders()
        headers.forEach { (key, values) ->
            if (key.lowercase() in SENSITIVE_HEADERS) {
                filteredHeaders.add(key, "***REDACTED***")
            } else {
                filteredHeaders.addAll(key, values)
            }
        }
        return filteredHeaders
    }

}



//inline fun test(crossinline a: () -> String, noinline s:() -> String) {
//
//}
//
// lateinit var a: String
//suspend fun test2() {
//
//
//}
//
//fun main() {
//
//    a = ""
//    println(::a.isInitialized)
//}
