package com.crash.course.ktdemoweb0

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableDiscoveryClient
class KtDemoWeb0Application

fun main(args: Array<String>) {
    runApplication<KtDemoWeb0Application>(*args)
}



@RestController
class HomeController {

    companion object {
        private val log = LoggerFactory.getLogger(HomeController::class.java)
    }

    @GetMapping("/nacos/hi")
    suspend fun hi(): String {
        log.info("i'm a thread start ${Thread.currentThread().name}")
        delay(1000)
        log.info("i'm a thread end ${Thread.currentThread().name}")
        Thread.startVirtualThread { log.info("i'm a virtual thread") }
        return "hi~"
    }
}