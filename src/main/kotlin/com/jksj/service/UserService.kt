package com.jksj.service

import com.jksj.model.Identity
import com.jksj.model.Login
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserService {

    private val log = KotlinLogging.logger {}

    fun login(login: Login): Identity {
        val ar = arrayOf(1, 2, 3)
        log.debug { login }
        log.info { login }

        val id = Identity("xxxid", "my_name", "my_token", "", "staff")
        return id
    }

    fun logout(login: Login): String {
        TODO("Not yet implemented")
    }

}
