package com.crash.course.ktdemoweb0


fun <T> T?.validate(condition: (T) -> Boolean, message: String = "Invalid input", action: () -> Unit = { null }): T? {
    if (this == null || !condition(this)) {
        println(message)
        action()
    }
    return this
}

fun main() {
    // 读取用户输入
    val name = readln()
    println("Hello, $name")

    val optionalInput = readlnOrNull()
    println("Hello, $optionalInput")

    // 基础数值操作
    val i: Int = 0
    println("i: $i")

    // 链式调用优化：计算 (0 / 2) + 2 - 1 % 3 = 0 + 2 - 1 = 1
    val calculationResult = i.div(2).plus(2).minus(1).mod(3)
    println(calculationResult)

    // 条件表达式优化
    println(if (i == 0) "i is zero" else "i is not zero")

    // 处理可能为null的数值
    val inputNumber = readln()
        .toIntOrNull()
        .validate({ it != 0 }, "Invalid or zero input").let {
            it ?: return
        }

    println("inputNumber: $inputNumber")
    val z = inputNumber + 1
    println("z: $z")

    // 浮点数处理
    val floatInput = readln().toFloatOrNull()
    val fRes = if (z != 0 && floatInput != null) floatInput / z else null
    println("fRes: $fRes")
}
