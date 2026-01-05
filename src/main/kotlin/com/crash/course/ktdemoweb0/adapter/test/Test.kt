package com.crash.course.ktdemoweb0.adapter.test

import com.crash.course.ktdemoweb0.config.api.HttpTestApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * @Author: xin yi
 * @Date 2026/1/5 15:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/test")
class Test (
    private val httpTestApi: HttpTestApi
){

    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(Test::class.java)
    }




    @GetMapping("/get")
    fun get(): String {
        log.info("i'm a thread start ${Thread.currentThread().name}")
        return "get"
    }

    @GetMapping("/webClient")
    fun testWebClient(): Mono<String> {
        return httpTestApi.get().map { result -> "httpTestApi res: $result" }
    }

}