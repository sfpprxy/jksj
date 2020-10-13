package com.jksj.common

import java.security.MessageDigest
import java.util.*
import javax.xml.bind.DatatypeConverter

class Timer {
    var start = 0L
    var last = 0L

    init {
        start = System.nanoTime()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    inline fun nanoTime(): Long {
        if (last == 0L) {
            last = start
        }
        val now = System.nanoTime()
        val dur = now - last
        last = now

        return dur
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getMills(): Double {
        return nanoTime().toDouble() / 1000000
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getSeconds(): Double {
        return getMills() / 1000
    }

}

fun newTimer(): Timer {
    return Timer()
}

fun sha1(input: String) = hashString("SHA-1", input)
fun md5(input: String) = hashString("MD5", input)

private fun hashString(type: String, input: String): String {
    val bytes = MessageDigest
        .getInstance(type)
        .digest(input.toByteArray())
    return DatatypeConverter.printHexBinary(bytes).toUpperCase()
}

fun uuid(): String {
    return UUID.randomUUID().toString()
}

