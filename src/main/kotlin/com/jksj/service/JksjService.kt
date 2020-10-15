package com.jksj.service

import com.jksj.api.JksjApi
import io.quarkus.runtime.Quarkus
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JksjService : JksjApi {

    private val log = KotlinLogging.logger {}

    override fun shutdown() {
        log.info { "prepare to shutdown..." }
        Quarkus.blockingExit()
    }

    override fun testable(): String {
        return "ok"
    }

}
