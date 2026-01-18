package com.crash.course.ktdemoweb0.formalapi

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

lateinit var a: String

suspend fun main() {

	CoroutineScope(Dispatchers.IO + CoroutineName("test")).launch {
		println("test")
	}.join()

	listOf(1, 2, 3).forEach {
		if (it == 2) {
			return@forEach
		}
//		if (it == 2) return
	}
	println("test")


}

val s: String = str() ?: throw Exception()

fun str() = "123"

fun str(a:String="") {
	TODO()
}

fun str1() {
	error("1")
}

@JvmOverloads
fun overMethod(a: Int = 0, b: Int = 0): Int {
	return a + b
}