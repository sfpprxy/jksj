package com.jksj.api

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
interface DepartmentApi {

    @POST @Path("/department/update_all")
    fun updateAll()

}
