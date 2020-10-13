package com.jksj.common

import kotlin.random.Random

class JksjException(message: String, var errorType: ErrorType) : RuntimeException(message)

inline fun err(message: Any, errorType: ErrorType): Nothing = throw JksjException(message.toString(), errorType)

inline fun exp(message: Any, errorType: ErrorType): JksjException = JksjException(message.toString(), errorType)

enum class ErrorType {
    UNAUTHORIZED, WRONG_INPUT
}

data class ErrorResp(
    var error: String?,
    var code: String,
    var uuid: String = uuid(),
    var shortId: Int = Random.nextInt(1000, 9999)
) {
    override fun toString(): String {
        return "code='$code' \n uuid='$uuid' \n shortId=$shortId \n error=$error"
    }
}
