package com.jksj

import com.jksj.model.Identity
import com.jksj.model.Login
import com.jksj.model.RemoteWorkApply
import com.jksj.service.UserService
import mu.KotlinLogging
import java.util.*
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MediaType.TEXT_PLAIN


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class JksjApi {

    private val log = KotlinLogging.logger {}

    @Inject
    @field: Default
    lateinit var userService: UserService

    @GET @Path("/hello")
    @Produces(TEXT_PLAIN)
    fun hello(): String {
        log.info { "this is INFO" }
        log.debug { "this is DEBUG" }
        return "hello"
    }

    @POST @Path("/login")
    fun login(login: Login): Identity {
        return userService.login(login)
    }

    @POST @Path("/logout")
    fun logout(login: Login): String {
        return "ok"
    }

    @POST @Path("/apply_remote_work")
    fun applyRemoteWork(remoteWorkApply: RemoteWorkApply): String {
        return Date().toString()
    }

    @POST @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    fun test(identity: Identity): String {
        val ar = arrayOf(1, 2, 3)
        return "asdhkjasdh"
    }

}
