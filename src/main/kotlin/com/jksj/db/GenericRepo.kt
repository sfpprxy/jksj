package com.jksj.db

import com.jksj.common.AllOpen
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Table
import javax.inject.Inject

@AllOpen
class GenericRepo<R : Record>(val table: Table<R>) {

    @Inject
    lateinit var dsl: DSLContext

    fun merge(pojo: Any) {
        val record = dsl.newRecord(table, pojo)
        dsl.insertInto(table)
            .set(record)
            .onDuplicateKeyUpdate()
            .set(record)
            .execute()
    }

}
