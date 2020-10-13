package com.jksj.db

import com.jksj.jooq.tables.daos.DepartmentDao
import mu.KotlinLogging
import org.jooq.DSLContext
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DepartmentRepo {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var dsl: DSLContext

    fun dao(): DepartmentDao {
        return DepartmentDao(dsl.configuration())
    }

}
