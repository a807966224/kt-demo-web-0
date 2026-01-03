package com.crash.course.ktdemoweb0

fun main() {
    repeat(5, { println("Hello World") })
    repeat(20.minus(readlnOrNull().let { it?.toInt() ?: 0 })) {
        println("Hello World")
    }

    for (i in 1..5 step 2) println("i : $i")


    if (readlnOrNull().let { (it?.toInt() ?: 0) > 5 }  ) println("Greater than 5")

    when (readlnOrNull()) {
        "1" -> println("One")
        "2" -> println("Two")
        "3" -> println("Three")
        else -> println("Invalid input")
    }

    readlnOrNull()?.toCharArray()?.also {
        for (i in it.indices) {
            if (it[i].isDigit()) println("Digit : ${it[i]}")
            else if (it[i].isLetter()) println("Letter : ${it[i]}")
            else println("Char : ${it[i]}")
        }
    }
}