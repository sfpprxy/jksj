package com.jksj.api

import com.jksj.model.*
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
interface UserApi {

    @GET @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        return "hello"
    }

    @POST @Path("/login")
    fun login(login: Login): Identity

    @POST @Path("/logout")
    fun logout()

    @POST @Path("/user/get_info")
    fun getInfo(): UserInfo

    @POST @Path("/user/update_all")
    fun updateAll()

    @POST @Path("/remote_work/status")
    fun getRemoteWorkStatus(): RemoteWorkStats

    @POST @Path("/remote_work/apply")
    fun applyRemoteWork(remoteWorkApply: RemoteWorkApply): RemoteWorkStats

}
