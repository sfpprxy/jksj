package com.jksj.common

import com.fasterxml.jackson.core.JsonParseException
import com.google.gson.Gson
import mu.KotlinLogging
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject
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

    override fun toResponse(exp: Exception): Response {
        val code: Int
        val error: ErrorResp
        when (exp) {
            is JksjException -> {
                code = 400
                error = ErrorResp(exp.message, "ERR_" + exp.errorType.name)
                log.info { "$error ${exp.stackTrace[0]} ${exp.message} ${requestFilter.getHTTPInput()}" }
            }
            is JsonParseException -> {
                code = 400
                error = ErrorResp("json格式错误: " + exp.message, "ERR_JSON_FORMAT")
                log.info { "$error ${exp.stackTrace[0]} ${exp.message} ${requestFilter.getHTTPInput()}" }
            }
            else -> {
                code = 500
                error = ErrorResp("服务器内部错误，请联系管理员", "ERR_UNKNOWN")
                log.error { "$error ${getExpEntity(exp)} ${requestFilter.getHTTPInput()}" }
            }
        }

        return Response.status(code)
            .entity(Gson().toJson(error))
            .build()
    }

}
