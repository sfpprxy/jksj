//package com.jksj.service
//
//import mu.KotlinLogging
//import org.flywaydb.core.Flyway
//
//import javax.enterprise.context.ApplicationScoped
//import javax.inject.Inject
//
//@ApplicationScoped
//class MigrationService {
//
//    private val log = KotlinLogging.logger {}
//
//    @Inject
//    lateinit var flyway: Flyway
//
//    fun checkMigration() {
//        log.info { "db version: ${flyway.info().current().version}" }
//    }
//
//}
