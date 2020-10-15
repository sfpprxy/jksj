package com.jksj.db

import com.jksj.jooq.Tables.DEPARTMENT
import com.jksj.jooq.tables.daos.DepartmentDao
import com.jksj.jooq.tables.records.DepartmentRecord
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DepartmentRepo : GenericRepo<DepartmentRecord>(DEPARTMENT) {

    private val log = KotlinLogging.logger {}

    fun dao(): DepartmentDao {
        return DepartmentDao(dsl.configuration())
    }

}
