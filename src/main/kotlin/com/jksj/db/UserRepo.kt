package com.jksj.db

import com.jksj.common.ErrorType
import com.jksj.common.exp
import com.jksj.jooq.tables.AppUser.APP_USER
import com.jksj.jooq.tables.daos.AppUserDao
import com.jksj.jooq.tables.pojos.AppUser
import com.jksj.jooq.tables.records.AppUserRecord
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepo : GenericRepo<AppUserRecord>(APP_USER) {

    private val log = KotlinLogging.logger {}

    fun dao(): AppUserDao {
        return AppUserDao(dsl.configuration())
    }

    fun findByLoginNameAndPassword(loginName: String, password: String): AppUser {
        return dsl.selectFrom(APP_USER)
            .where(APP_USER.OA_LOGIN_NAME.eq(loginName))
            .and(APP_USER.PASSWORD.eq(password))
            .fetchOptional()
            .orElseThrow { exp("登录id $loginName 用户名或密码错误", ErrorType.WRONG_INPUT) }
            .into(AppUser::class.java)
    }

    fun saveAll(newUsers: List<AppUser>) {
        val users = newUsers.map {
            dsl.newRecord(APP_USER, it)
        }
        val execute = dsl.batchStore(users).execute()
        log.info { "executed $execute" }
    }

}
