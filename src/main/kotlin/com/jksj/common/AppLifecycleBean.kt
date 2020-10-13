package com.jksj.common

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class AppLifecycleBean {

    private val log = KotlinLogging.logger {}

    fun onStart(@Observes ev: StartupEvent?) {
        log.info { "The application is starting..." }
        // todo load caches to db
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        log.info { "The application is stopping..." }
        // todo flush caches to db
    }

}
