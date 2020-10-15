package com.jksj.common

import com.jksj.service.UserService
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject

@ApplicationScoped
class AppLifecycleBean {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var userService: UserService

    fun onStart(@Observes ev: StartupEvent?) {
        log.debug { "Application log debug level enabled" }
        userService.loadUserSessions()
        userService.loadRemoteWorkSessions()
        log.info { "Application started" }
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        log.info { "The application is stopping..." }
        userService.persistUserSessions()
        userService.persistRemoteWorkSessions()
        log.info { "Application gracefully stopped" }
    }

}
