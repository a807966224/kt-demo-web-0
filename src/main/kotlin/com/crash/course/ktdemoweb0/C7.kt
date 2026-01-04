package com.crash.course.ktdemoweb0

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors

object C7 {

    val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())

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

    private val _hotFlow = MutableSharedFlow<Int>()
    suspend fun emitHotFlowItem(item: Int) {
        _hotFlow.emit(item)
    }
    val hotFlow = _hotFlow
        .shareIn(scope, SharingStarted.WhileSubscribed())

    // 推荐模式：内部使用Channel，对外暴露Flow
    class DataProcessor {
        private val _dataChannel = Channel<Data>()
        val dataFlow: Flow<Data> = _dataChannel.receiveAsFlow()

        suspend fun process() {
            // 内部使用Channel发送
            _dataChannel.send(processData())
        }

        private fun processData(): Data {
            return Data()
        }
    }

    // 对于大多数业务逻辑，使用SharedFlow替代Channel
    class ViewModel {
        private val _events = MutableSharedFlow<Data>()
        val events = _events.asSharedFlow()

        suspend fun emitEvent(event: Data) {
            _events.emit(event)
        }
    }

    data class Data(val str: String = "1")
}

@OptIn(FlowPreview::class)
fun main() {

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
        .retry(5)
        .catch {
            println("catch error $it")
        }
        .map {
            it * 2
        }
        .flowOn(Dispatchers.IO)
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

    // 修复：使用正确的flow引用
    val localFlow = flow {
        repeat(10) {
            emit(it)
            delay(100)
        }
    }

    C7.scope.launch {
        localFlow.collect {
            println("local flow item is $it")
            delay(1000)
        }
    }
}

val flow = flow {
    repeat(1000) {
        emit(it)
    }
}.buffer(capacity = 100)

@RestController
class C7Controller {

    @PostConstruct
    fun init() {
        val mutex = Mutex()
        C7.scope.launch {
            C7.hotFlow
                .collect {
                    mutex.withLock {
                        delay(1000)
                        println("hot flow item is $it")
                    }
                }
        }
    }

    @GetMapping("/c7")
    suspend fun get(): String {
        C7.emitHotFlowItem(1)
        return "ok"
    }
}

// 内联类，生成相关方法
inline class Data(val str: String)

// 值类，生成相关方法
@JvmInline
value class Data2(val str: String) {
    fun getStr(): String {
        return str
    }
}
