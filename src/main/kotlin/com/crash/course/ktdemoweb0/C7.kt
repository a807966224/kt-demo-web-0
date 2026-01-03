package com.crash.course.ktdemoweb0

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object C7 {

    val scope  = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())

    val channel = Channel<Int>()
    init {
        flow {
            channel.consumeEach {
                emit(it)
            }
        }.onEach {
            println("flow item is $it")
        }.launchIn(scope)
    }

}

@OptIn(FlowPreview::class)
fun main () {

    repeat(3, {
        generateSequence(0) {
            it + 1
        }
            .take(10)
            .forEach { println(it) }
    })



    // 支持协程
    // 冷流
    val flowOf = flowOf(1, 2, 3)
    flowOf.debounce { 500 }
        .map {
            it * 2
        }
        .onEach {
            println(it)
        }
        .launchIn(C7.scope)

    C7.scope.launch {
        C7.channel.send(1)
    }

    // 热流
    // .asSharedFlow()当使用共享流修饰，则不允许外部调用
    val hotFlow = MutableSharedFlow<Int>()

    hotFlow
        .onEach { println("hot flow item is $it") }
        .launchIn(C7.scope)

    Thread.sleep(1000)

    C7.scope.launch {
        hotFlow.emit(1)
    }






}


