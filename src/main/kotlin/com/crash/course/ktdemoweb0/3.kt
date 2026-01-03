package com.crash.course.ktdemoweb0

import java.io.File
import java.nio.file.Paths

// 主构造函数
class Student(var name: String, var age: Int = 0) {

    var address: String = ""
        set(value) {
            if (value.isEmpty()) {
                throw IllegalArgumentException("Address cannot be empty")
            }
            field = value
        }

    init {
        println("init")
    }

    override fun toString(): String {
        return "name: $name, age: $age, address: $address"
    }
}


// 单例模式，适用无构造参数适用object关键字
object StudentManager {
    fun getStudent(): Student {
        return Student("lisi", 18)
    }

    // 后备字段, 保护原有字段
    private val _a = mutableListOf(11, 2, 3)
    val a get() = _a.toList()
}

enum class Color {
    RED, GREEN, BLUE
}

// 继承，多态
// 封装

data class Person(val name: String, val age: Int) {
    val isAdult: Boolean
        get() = age >= 18
}


// Nothing 是所有类型的子类型，表示永不返回的函数
// Unit 是表示无返回值的类型，相当于 Java 中的 void
fun testThrowException() : Nothing {
    throw IllegalArgumentException("参数错误")
}

abstract class BaseActivity : BaseView{
    abstract fun initView()
    abstract fun initData()
    abstract fun initListener()
}

interface BaseView {
    fun showLoading()
    fun hideLoading()
}

class MainActivity : BaseActivity() {
    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }

    override fun initListener() {
        TODO("Not yet implemented")
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }
}

fun main () {

    Student("zhang san", 18).apply {
        println(name)
        println(age)
        this.address = "beijing"
        println(address)
        println(this)
    }
    Student(name = "zhang san").also {
        println(it.name)
        println(it.age)
    }

    Student("zhang san", 18).let {
        println(it.name)
        println(it.age)
    }

    Student("zhang san", 18).run {
        println(name)
        println(age)
    }

    Student("zhang san", 18).also {
        println(it.name)
        println(it.age)
    }.apply {
        println(name)
        println(age)
    }

    Color.GREEN.let {
        println("Color.Green : ${it.name}, ${it.ordinal}")
    }

    // 安全的文件操作
    try {
        val fileName = "a.txt"
        // 验证文件路径，防止路径遍历
        val normalizedPath = Paths.get(fileName).normalize().toString()
        if (!normalizedPath.equals(fileName)) {
            throw SecurityException("Invalid file path: $fileName")
        }

        val file = File(fileName)
        file.apply {
            println(name)
            println(absolutePath)
        }.also {
            println(it.name)
            println(it.absolutePath)
            it.appendText("hello world")
        }.also {
            // 限制文件大小以防止内存溢出
            if (it.length() <= 10 * 1024 * 1024) { // 10MB limit
                println(it.readText())
            } else {
                println("File too large to read")
            }
        }
    } catch (e: SecurityException) {
        println("Security error: ${e.message}")
    } catch (e: Exception) {
        println("File operation error: ${e.message}")
    }

    StudentManager.let {
        println(it.a)
        println(it.getStudent())
    }

    Person("li si", 18).copy(name = "wang wu").apply {
        println(isAdult)
        println(name)
    }

    // Elvis 运算符
    val name = readlnOrNull()
    println(name?.plus("zhangsan") ?: "lisi")

    fun innerFun() {
        println("inner fun... return Kt Unit")
    }

    // Unit 代表无方法返回值
    val innerFun = innerFun()
    println(innerFun.hashCode())

    testThrowException()

}
