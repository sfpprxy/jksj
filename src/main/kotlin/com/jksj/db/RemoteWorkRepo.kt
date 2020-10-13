package com.jksj.db

import com.jksj.jooq.Tables.REMOTE_WORK
import com.jksj.jooq.tables.daos.RemoteWorkDao
import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.sum
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class RemoteWorkRepo {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var dsl: DSLContext

    fun dao(): RemoteWorkDao {
        return RemoteWorkDao(dsl.configuration())
    }

    fun getTimeUsedByUser(id: String): Pair<Int, Long> {
        val res = dsl.select(count(), sum(REMOTE_WORK.TIME_APPLIED))
            .from(REMOTE_WORK)
            .where(REMOTE_WORK.USER_ID.eq(id))
            .fetchOne()

        val timeUsed = res.value1()
        val timeUsedSec = res.value2().toLong()

        return Pair(timeUsed, timeUsedSec)
    }

}
