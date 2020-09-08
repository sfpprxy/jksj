package com.jksj

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import org.slf4j.LoggerFactory
import java.util.*

object Main {
  private val log = LoggerFactory.getLogger(Main::class.java)

  @JvmStatic
  fun main(args: Array<String>) {
    Quarkus.run(JksjServer::class.java, *args)
  }

  class JksjServer : QuarkusApplication {
    @Throws(Exception::class)
    override fun run(vararg args: String): Int {
      log.info("application started at {}", Date())
      log.debug("application log debug level enabled")
      Quarkus.waitForExit()
      return 0
    }
  }

}
