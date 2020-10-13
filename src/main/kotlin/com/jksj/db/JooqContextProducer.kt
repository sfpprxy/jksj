package com.jksj.db

import io.agroal.api.AgroalDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class JooqContextProducer {

    @Inject
    lateinit var dataSource: AgroalDataSource

    @ApplicationScoped
    fun dslContext(): DSLContext {
        return DSL.using(dataSource, SQLDialect.POSTGRES)
    }

}
