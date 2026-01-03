package com.crash.course.ktdemoweb0

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException

object C6 {
    // kt的结构化并发原则 - 使用可关闭的线程池
    private val singleThreadExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "coroutine-thread-1").apply { isDaemon = true }
    }
    val scope = CoroutineScope(SupervisorJob() + CoroutineName("coroutine-name-1") + singleThreadExecutor.asCoroutineDispatcher())

    // 调度器
    val scope1 = CoroutineScope(Job() + CoroutineName("coroutine-name-2") + Dispatchers.IO)

    val scope2 = CoroutineScope(CoroutineName("coroutine-name-3") + Dispatchers.Default)

    val scope3 = CoroutineScope(CoroutineName("coroutine-name-4") + Dispatchers.Unconfined)

    // 线程池化的异常处理器作用域
    private val cachedThreadPoolExecutor = Executors.newCachedThreadPool { r ->
        Thread(r, "coroutine-thread-pool").apply { isDaemon = true }
    }
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught $throwable")
    }
    val scope5 = CoroutineScope(exceptionHandler + CoroutineName("scope5") + cachedThreadPoolExecutor.asCoroutineDispatcher())

    // 提供关闭方法
    fun close() {
        singleThreadExecutor.shutdown()
        cachedThreadPoolExecutor.shutdown()
        scope.cancel()
        scope1.cancel()
        scope2.cancel()
        scope3.cancel()
        scope5.cancel()
    }
}

class C6_Person(val name: String) {
    override fun toString(): String = "C6_Person(name='$name')"
}

fun main() = runBlocking {
    println("outer start...")

    val launch = C6.scope.launch {
        println("start...")
        delay(1000)
        println("end...")

        withContext(C6.scope1.coroutineContext) {
            println("start...")
        }
    }

    C6.scope.launch {
        launch.join()
        println("outer end...")
    }

    println("outer end...")

    val mutableListOf = mutableListOf<Deferred<C6_Person>>()

    C6.scope.async {
        println("start...")
        delay(1000)
        println("end...")
        C6_Person("zhangsan")
    }.also { mutableListOf.add(it) }

    C6.scope.launch {
        mutableListOf.awaitAll().forEach { println(it) }
    }

    val p_job = C6.scope.launch {
        val job1 = launch {
            println(coroutineContext.job?.parent)
            printNums()
        }

        val job2 = launch {
            println(coroutineContext.job?.parent)
            printNums()
        }
    }

    println(p_job.job)

    delay(3000) // 使用delay替代Thread.sleep

    p_job.cancel()

    C6.scope5.launch {
        // 当子协程抛出异常时，父协程也会被取消，同时父协程下面的子协程都会取消
        // 子协程不可以传递异常处理器， 不会生效
        launch {
            delay(1000)
            error1()
        }

        launch {
            delay(5000)
            println(111111)
        }
    }

    runCatching {
        error2()
    }.onFailure {
        println("Caught $it")
    }.onSuccess {
        println("success")
    }

    // 协程取消，如果协程内进行了try catch，那么一定要额外处理取消协程异常
    val cancelJob = C6.scope.launch {
        try {
            // 确定协程是否正常
            ensureActive()
            while (isActive) { // 使用isActive替代无限循环
                delay(1000)
                println("running...")
            }
        } catch (e: CancellationException) {
            println("Caught $e")
            throw e // CancellationException应该重新抛出
        } catch (e: Exception) {
            println("Caught unexpected exception: $e")
            throw e
        }
    }

    delay(3000) // 使用delay替代Thread.sleep
    cancelJob.cancel()

    val async = C6.scope3.async {
        suspendCancellableCoroutine<C6_Person> { continuation ->
            continuation.resumeWith(Result.success(C6_Person("11")))
        }
    }

    // 等待async完成
    try {
        println(async.await())
    } catch (e: Exception) {
        println("Async failed: $e")
    }

    // 在程序结束前关闭资源
    delay(1000) // 给其他协程一些时间完成
    C6.close()
}

fun error2() {
    error("error2~~~")
}

suspend fun error1() {
    error("error1~~~")
}

suspend fun printNums() {
    var i = 0
    // 使用isActive检查协程状态
    while (true) {
        println(i++)
        delay(1000)
    }
}
