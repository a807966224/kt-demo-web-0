package com.crash.course.ktdemoweb0.config.api

import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

/**
 * @Author: xin yi
 * @Date 2026/1/5 14:58
 * @Version 1.0
 */
@HttpExchange("/test")
interface HttpTestApi {

    @GetExchange("/get")
    fun get(): Mono<String>

}