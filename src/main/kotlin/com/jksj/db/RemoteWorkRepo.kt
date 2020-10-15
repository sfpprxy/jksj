package com.jksj.db

import com.jksj.jooq.Tables.REMOTE_WORK
import com.jksj.jooq.tables.daos.RemoteWorkDao
import com.jksj.jooq.tables.records.RemoteWorkRecord
import mu.KotlinLogging
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.sum
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RemoteWorkRepo : GenericRepo<RemoteWorkRecord>(REMOTE_WORK) {

    private val log = KotlinLogging.logger {}

    fun dao(): RemoteWorkDao {
        return RemoteWorkDao(dsl.configuration())
    }

    fun getTimeUsedByUser(id: String): Pair<Int, Long> {
        val res = dsl.select(count(), sum(REMOTE_WORK.TIME_APPLIED))
            .from(REMOTE_WORK)
            .where(REMOTE_WORK.USER_ID.eq(id))
            .fetchOne()

        val timeUsed = res.value1() ?: 0
        val timeUsedSec = res.value2()?.toLong() ?: 0

        return Pair(timeUsed, timeUsedSec)
    }

}
