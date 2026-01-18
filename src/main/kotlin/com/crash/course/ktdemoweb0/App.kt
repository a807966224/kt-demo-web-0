package com.crash.course.ktdemoweb0

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

/**
 * @Author: xin yi
 * @Date 2026/1/5 17:12
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}