package com.jksj.common

import io.vertx.core.http.HttpServerRequest
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo
import javax.ws.rs.ext.Provider

@Provider
@ApplicationScoped
class RequestFilter : ContainerRequestFilter {

    private val log = KotlinLogging.logger {}

    @Context
    lateinit var uriInfo: UriInfo

    @Context
    lateinit var request: HttpServerRequest

    override fun filter(context: ContainerRequestContext) {
        val method = context.method
        val path = uriInfo.path
        val address = request.remoteAddress().toString()
        val token = request.getHeader("token")
        val xForwardedFor = request.getHeader("X-Forwarded-For") ?: address
        log.info { "Request $method $path from IP $xForwardedFor with token $token" }
    }

    fun getHTTPInput(): String {
        val address = request.remoteAddress().toString()
        val uri = request.method().toString() + " " + request.uri()
        val headers = request.headers()
        //todo log body as well
        return listOf(address, uri, headers).joinToString(separator = "\n", prefix = "\n======HTTP INPUT======\n")
    }

    fun token(): String {
        return request.getHeader("token") ?: run {
            err("找不到token", ErrorType.UNAUTHORIZED)
        }
    }

}
