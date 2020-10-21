package com.jksj.common

import com.fasterxml.jackson.core.JsonParseException
import com.google.gson.Gson
import mu.KotlinLogging
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject
import javax.ws.rs.NotAllowedException
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class AppExceptionMapper : ExceptionMapper<Exception> {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var requestFilter: RequestFilter

    private fun getExpEntity(exception: Throwable): Any? {
        // return stack trace for debugging (probably don't want this in prod...)
        val errorMsg = StringWriter()
        exception.printStackTrace(PrintWriter(errorMsg))
        return errorMsg.toString()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun newErrResp(exp: Exception, bizMsg: String, type: String): ErrorResp {
        return ErrorResp(bizMsg + " " + exp.message, "ERR_$type")
    }

    override fun toResponse(exp: Exception): Response {
        val code: Int
        val error: ErrorResp
        when (exp) {
            is JksjException -> {
                code = 400
                error = newErrResp(exp, "", exp.errorType.name)
            }
            is JsonParseException -> {
                code = 400
                error = newErrResp(exp, "json格式错误: ", "JSON_FORMAT")
            }
            is NotAllowedException -> {
                code = 400
                error = newErrResp(exp, "HTTP方法错误，请用POST", ErrorType.WRONG_INPUT.name)
            }
            is NotFoundException -> {
                code = 400
                error = newErrResp(exp, "URL错误，请检查路径 ", ErrorType.WRONG_INPUT.name)
            }
            else -> {
                code = 500
                error = ErrorResp("服务器内部错误，请联系管理员", "ERR_UNKNOWN")
            }
        }
        when (code) {
            400 -> log.info { "$error ${exp.stackTrace[0]} ${exp.message} ${requestFilter.getHTTPInput()}" }
            500 -> log.error { "$error ${getExpEntity(exp)} ${requestFilter.getHTTPInput()}" }
        }

        return Response.status(code)
            .entity(Gson().toJson(error))
            .build()
    }

}
