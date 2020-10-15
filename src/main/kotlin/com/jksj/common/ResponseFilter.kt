package com.jksj.common

import com.google.gson.Gson
import mu.KotlinLogging
import javax.inject.Inject
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

@Provider
class ResponseFilter : ContainerResponseFilter {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var requestFilter: RequestFilter

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
        val entity = responseContext!!.entity ?: return
        val str = entity.toString()
        var error: ErrorResp? = null

        if (str.contains("Unrecognized field")) {
            val sub = str.substringAfter("Unrecognized field").split(" ")[1]
            error = ErrorResp("检查到未定义字段$sub, 请检查json", "ERR_JSON_FORMAT")
        }

        if (error != null) {
            log.info { "$error ${requestFilter.getHTTPInput()}" }
            responseContext.headers.putSingle("Content-Type", "application/json")
            responseContext.entity = Gson().toJson(error)
        }
    }

}
