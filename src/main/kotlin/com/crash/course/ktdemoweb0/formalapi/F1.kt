package com.crash.course.ktdemoweb0.formalapi

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.TimeZone
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.TestTimeSource
import kotlin.time.asClock
import kotlin.time.measureTime

fun Throwable.addSuppressions(vararg suppressions: String) {
	for (s in suppressions) {
		addSuppressed(Exception(s))
	}
}

fun testVarArag() {
	try {
		throw Exception("test")
	} catch (e: Exception) {
		e.addSuppressions("1", "2", "3")
	}
}


inline fun <T> T.MyAlso(noinline block: (T) -> Unit): T {
	this.MyAlso2(block)
	println("MyAlso ...")
	return this
}

fun <T> T.MyAlso2(block: (T) -> Unit): T {
	block.invoke(this)
	println("MyAlso V2....")
	return this
}

infix
inline fun BigInteger.MyAdd(other: BigInteger): BigInteger {
	BigDecimal.ONE
	return this.add(other)
}

fun testCollection() {
	mutableListOf<Int>().also {
		it.add(1)
	}.also {
		it.forEach { println(it) }
	}

	mutableMapOf<String, Int>().also {
		it["k1"] = 1
		it["k2"] = 2
	}.also {
		it.forEach { println(it) }
	}


}


fun main(args: Array<String>) {
	measureTime {
		println(args)
		println(message = BigInteger.ZERO.MyAdd(BigInteger.ONE))
		println(message = BigInteger.ZERO MyAdd BigInteger.ONE)
		"".MyAlso { println("test") }
			.also { println(it) }
		testVarArag()


		val d : Double = 1.0
		println(d.pow(9))

		val now = Clock.System.now()
		val timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)


	}.also { println("thisTotaled $it Milliseconds") }

	testCollection()

	MyCollection().list2.forEach { println(it) }


	MySequence().sequence
		.plus(listOf(1,2,3,4,5))
		.map { it * 2 }
		.filter { it > 2 }
		.take(8)
		.forEach { println(it) }

	println(Person().extName)

	println("${MyDataObj.age} : ${MyObject.test()}")
	Person.test()

	runBlocking {
		MyFlow().flow
			.onEach { delay(800) }
			.collect { println(it) }
	}

	when (readln() as Any) {
		is String -> println("string...")
		else -> println("more args")
	}
}

// 对象
object MyObject {
	fun test() {
		println("test delay My Object")
	}
}

data object MyDataObj {
	val age : Int = 19
}

// 内连值类型
@JvmInline
value class Email(val email : String)

// 扩展属性
val Person.extName: String
	get() = name.uppercase()


class Person {

	companion object {
		fun test() {
			println("test companion object ....")
		}
	}

	var name: String = ""
		set(value) {
			field = value.uppercase()
		}
}

class MyFlow {
	val flow = flow {
		repeat(1000) {
			emit(it)
			delay(1000)
		}
	}
}

class MySequence {
	// 节省内存，延迟执行，中间不产生额外的数据集合，直到终止端才开始执行
	// 无法重复执行，无法索引数据
	val sequence = sequence {
		yield(1)
		yield(2)
		yield(3)
	}
//	val list = sequence.toList()
}

class MyCollection {
	private val _list = mutableListOf<Int>().also {
		it.add(1)
		it.add(2)
	}
	init {
		_list.add(3)
		_list.add(4)
	}

	val list: List<Int> = _list

	// 上边的写法不好,不可变性增强：确保内部集合真正不可变，防止外部修改
	// 初始化优化：统一初始化逻辑，提高代码可读性

	private val _list2 = mutableListOf<Int>().run {
		add(1)
		add(2)
		this.toList()
	}

	val list2 : List<Int> = _list2

}

