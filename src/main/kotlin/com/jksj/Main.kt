package com.jksj

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain

@QuarkusMain
class Main {

    class JksjServer : QuarkusApplication {
        @Throws(Exception::class)
        override fun run(vararg args: String): Int {
            Quarkus.waitForExit()
            return 0
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Quarkus.run(JksjServer::class.java, *args)
        }
    }

}
