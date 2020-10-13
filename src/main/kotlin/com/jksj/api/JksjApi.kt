package com.jksj.api

import com.jksj.model.Login
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
interface JksjApi {

    @POST @Path("/test0")
    @Produces(MediaType.TEXT_PLAIN)
    fun test(login: Login): String {
        return "test ok"
    }

}
