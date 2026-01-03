package com.crash.course.ktdemoweb0

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.time.measureTime

// 伴生对象实现的单例模式
class Singleton {

    private constructor()
    private var _count = 0
        get() {
            field++
            return field
        }

    val count: Int
        get() = _count

    companion object {
        val instance: Singleton by lazy { return@lazy Singleton() }
    }

}

// 函数式接口
fun
interface Callback {
    fun callback()
}

// 密封接口
sealed interface Base {
    object Success : Base
}



fun main() {
    val listOf = listOf<Int>(1, 2, 3, 4, 5, 6)
    listOf
        .filter { it > 2 }
        .map { it * 2 }
        .forEach { println(it) }

    measureTime {
        var i = 0
        while (i < 99999999 ) {
            i++
        }
    }.let {
        println(it)
    }


    thread {
        run {
            while (true) {
                Thread.sleep(1000)
                println("Thread")
            }
        }
    }

    repeat(3, { println(Singleton.instance.count) })

    Callback::callback {
        println("callback")
    }

    // 结构化并发原则
    // 1.
    GlobalScope.launch {
        testStop()
    }

    Executors.newSingleThreadExecutor().execute {
        while (true) {
            Thread.sleep(1000)
            println("Thread")
        }
    }




}




suspend fun testStop() {
    delay(1000)
    println("GlobalScope")
}