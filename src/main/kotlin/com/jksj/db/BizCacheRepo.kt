package com.jksj.db

import com.jksj.jooq.Tables.BIZ_CACHE
import com.jksj.jooq.tables.daos.BizCacheDao
import com.jksj.jooq.tables.records.BizCacheRecord
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BizCacheRepo : GenericRepo<BizCacheRecord>(BIZ_CACHE) {

    private val log = KotlinLogging.logger {}

    fun dao(): BizCacheDao {
        return BizCacheDao(dsl.configuration())
    }

}
