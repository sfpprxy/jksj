package com.jksj.service

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jksj.api.UserApi
import com.jksj.common.*
import com.jksj.db.BizCacheRepo
import com.jksj.db.RemoteWorkRepo
import com.jksj.db.UserRepo
import com.jksj.jooq.tables.pojos.AppUser
import com.jksj.jooq.tables.pojos.BizCache
import com.jksj.jooq.tables.pojos.RemoteWork
import com.jksj.model.*
import io.quarkus.scheduler.Scheduled
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URL
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class UserService : UserApi {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var remoteWorkRepo: RemoteWorkRepo

    @Inject
    lateinit var bizCacheRepo: BizCacheRepo

    private val threadPool: ExecutorService = Executors.newCachedThreadPool()

    val userSessions: Cache<String, AppUser> = CacheBuilder.newBuilder()
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .maximumSize(500)
        .expireAfterAccess(1, TimeUnit.DAYS)
        .build()

    val MAX_HOURS = 4L

    val remoteWorkSessions: Cache<String, RemoteWorkStats> = CacheBuilder.newBuilder()
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .expireAfterWrite(MAX_HOURS, TimeUnit.HOURS)
        .build()

    @Inject
    lateinit var requestFilter: RequestFilter

    @ConfigProperty(name = "wudi.token")
    lateinit var wudiToken: String

    @ConfigProperty(name = "wudi.password")
    lateinit var wudiPassword: String

    @ConfigProperty(name = "oa.url.get-user-list")
    lateinit var oaUrlGetUserList: String

    @ConfigProperty(name = "server.domain")
    lateinit var domain: String

    override fun login(login: Login): Identity {
        val user = userRepo.findByLoginNameAndPassword(login.username, login.password)
        val identity = Identity(md5(user.id + Instant.now().epochSecond.toString()))
        userSessions.put(identity.token, user)
        return identity
    }

    override fun logout() {
        userSessions.invalidate(requestFilter.token())
    }

    private fun getUserSession(): AppUser {
        // dev mode
        val token = requestFilter.token()
        if (token == wudiToken) {
            val user = userRepo.findByLoginNameAndPassword("testwudi", wudiPassword)
            userSessions.getIfPresent(token) ?: run {
                userSessions.put(token, user)
            }
        }

        return userSessions.getIfPresent(token) ?: err("用户未登录", ErrorType.UNAUTHORIZED)
    }

    override fun getInfo(): UserInfo {
        val user = getUserSession()
        return UserInfo(user.id, user.oaLoginName, user.oaRealName, user.oaDeptId)
    }

    private fun loadAllFromOA(): List<AppUser> {
        val body = URL(oaUrlGetUserList).readText()

        val type = object : TypeToken<OAResp<OAUser>>() {}.type
        val res = Gson().fromJson<OAResp<OAUser>>(body, type)

        return res.data.map {
            AppUser()
                .setOaId(it.id)
                .setOaDeleteFlag(it.deleteFlag)
                .setOaLoginName(it.loginName)
                .setOaRealName(it.realName)
                .setOaDeptId(it.departmentId)
                .setOaStatus(it.status)
                .setOaJkyFlag(it.jkyFlag)
                .setOaFinanceFlag(it.financeFlag)
                .setOaIdCard(it.idCard)
                .setOaPhone(it.phone)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun loadAllFromVH(users: List<AppUser>): List<AppUser> {
        TODO("later")
    }

    private fun setDefaultNewUserProperties(new: AppUser): AppUser {
        return new.setId(uuid())
            .setPassword("8888")
            .setRoleIds(arrayOf("basic", "staff"))
    }

    private fun setOAProperties(src: AppUser, oa: AppUser): AppUser {
        return src.setOaId(oa.oaId)
            .setOaDeleteFlag(oa.oaDeleteFlag)
            .setOaLoginName(oa.oaLoginName)
            .setOaRealName(oa.oaRealName)
            .setOaDeptId(oa.oaDeptId)
            .setOaStatus(oa.oaStatus)
            .setOaJkyFlag(oa.oaJkyFlag)
            .setOaFinanceFlag(oa.oaFinanceFlag)
            .setOaIdCard(oa.oaIdCard)
            .setOaPhone(oa.oaPhone)
    }

    @Scheduled(cron = "0 30 4 * * ?", every = "3600s")
    @Transactional
    override fun updateAll() {
        val oaUsers = loadAllFromOA()
        val dbUsers = userRepo.dao().findAll()
        val new = arrayListOf<AppUser>()
        val old = arrayListOf<AppUser>()
        for (oa in oaUsers) {
            val find = dbUsers.find { it.oaId == oa.oaId }
            if (find == null) {
                new.add(setOAProperties(setDefaultNewUserProperties(oa), oa))
                log.info { "prepare to add new user $oa" }
            } else {
                old.add(setOAProperties(find, oa))
            }
        }
        userRepo.dao().insert(new)
        userRepo.dao().update(old)
        log.info { "updateAll success: old ${old.size} new ${new.size}" }
    }

    override fun getRemoteWorkStatus(): RemoteWorkStats {
        val userSession = getUserSession()
        val remoteWork = remoteWorkSessions.getIfPresent(userSession.id) ?: RemoteWorkStats()

        val (timeUsed, timeUsedSec) = remoteWorkRepo.getTimeUsedByUser(userSession.id)
        remoteWork.timeUsed = timeUsed + 1
        remoteWork.timeUsedSec = timeUsedSec

        remoteWork.timeStart?.let {
            remoteWork.timeUsed -= 1
            val timeApplied = remoteWork.timeApplied!!
            val sessionTimeUsedSec = Instant.now().epochSecond - it
            if (sessionTimeUsedSec > timeApplied) {
                remoteWork.timeUsedSec = timeUsedSec
                remoteWork.timeRemaining = 0
            } else {
                remoteWork.timeUsedSec = sessionTimeUsedSec + timeUsedSec - timeApplied
                remoteWork.timeRemaining = timeApplied - sessionTimeUsedSec
            }
        }
        return remoteWork
    }

    private fun createFrpSession(seconds: Long): Int {
        // /root/frp_0.27.0_linux_amd64
        // timeout 3600 nohup /root/frp_0.27.0_linux_amd64/frpc tcp -l 888 -r 10559 -s sh.asdk.io:7000 -n jksj_10559 &
        val port = Random().nextInt(10000) + 10000
        val command = "timeout $seconds nohup " +
            "/root/frp_0.27.0_linux_amd64/frpc tcp -l 888 -r $port -s $domain:7000 -n jksj_$port" +
            " &"
        // todo do not close frp sessions when quit app
        threadPool.submit {
            ProcessBuilder(command.split(" ").toList())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(4, TimeUnit.HOURS)
        }
        return port
    }

    @Transactional
    override fun applyRemoteWork(remoteWorkApply: RemoteWorkApply): RemoteWorkStats {
        val hour = remoteWorkApply.hour
        if (hour % 0.5 != 0.0 || hour > 4) {
            err("时间必须是0.5的倍数且小于4", ErrorType.WRONG_INPUT)
        }

        val userSession = getUserSession()
        val remoteWork = getRemoteWorkStatus()
        if (remoteWork.timeRemaining > 0) {
            return remoteWork
        }

        log.info { "${userSession.oaRealName} apply_remote_work for $hour hour" }
        val seconds = (hour * 3600).toLong()
        val port = createFrpSession(seconds)

        val link = "http://$domain:$port"
        val newRemote = RemoteWorkStats(
            link, Instant.now().epochSecond, seconds, seconds,
            remoteWork.timeUsed, remoteWork.timeUsedSec
        )
        remoteWorkRepo.dao().insert(
            RemoteWork(
                uuid(),
                userSession.id,
                link,
                OffsetDateTime.now(),
                null,
                seconds.toInt(),
                null
            )
        )
        remoteWorkSessions.put(userSession.id, newRemote)
        return newRemote
    }

    fun persistUserSessions() {
        val json = Gson().toJson(userSessions.asMap())
        log.info { "persisting userSessions: $json" }
        bizCacheRepo.merge(BizCache("userSessions", json))
    }

    fun loadUserSessions() {
        val json = bizCacheRepo.dao().fetchOneById("userSessions").cacheStr ?: "{}"
        val type = object : TypeToken<ConcurrentMap<String, AppUser>>() {}.type
        val map = Gson().fromJson<ConcurrentMap<String, AppUser>>(json, type)
        userSessions.putAll(map)
    }

    fun persistRemoteWorkSessions() {
        val json = Gson().toJson(remoteWorkSessions.asMap())
        log.info { "persisting remoteWorkSessions: $json" }
        bizCacheRepo.merge(BizCache("remoteWorkSessions", json))
    }

    fun loadRemoteWorkSessions() {
        val json = bizCacheRepo.dao().fetchOneById("remoteWorkSessions")?.cacheStr ?: "{}"
        val type = object : TypeToken<ConcurrentMap<String, RemoteWorkStats>>() {}.type
        val map = Gson().fromJson<ConcurrentMap<String, RemoteWorkStats>>(json, type)
        remoteWorkSessions.putAll(map)
    }

}
