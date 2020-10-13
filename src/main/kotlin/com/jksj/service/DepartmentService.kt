package com.jksj.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jksj.api.DepartmentApi
import com.jksj.common.uuid
import com.jksj.db.DepartmentRepo
import com.jksj.jooq.tables.pojos.Department
import com.jksj.model.OADepartment
import com.jksj.model.OAResp
import io.quarkus.scheduler.Scheduled
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class DepartmentService : DepartmentApi {

    private val log = KotlinLogging.logger {}

    @Inject
    lateinit var departmentRepo: DepartmentRepo

    @ConfigProperty(name = "oa.url.get-dept-list")
    lateinit var oaUrlGetDeptList: String

    private fun loadAllFromOA(): List<Department> {
        val body = URL(oaUrlGetDeptList).readText()

        val type = object : TypeToken<OAResp<OADepartment>>() {}.type
        val res = Gson().fromJson<OAResp<OADepartment>>(body, type)

        return res.data.map {
            Department()
                .setOaId(it.id)
                .setOaDeptName(it.deptName)
                .setOaDeptCode(it.deptCode)
                .setOaDeleteFlag(it.deleteFlag)
                .setOaParentId(it.parentId)
                .setOaState(it.state)
                .setOaIsCompany(it.isCompany)
                .setOaSyncTime(it.syncTime)
        }
    }

    private fun loadAllFromVH(users: List<Department>): List<Department> {
        TODO("later")
    }

    private fun setDefaultNewDepartmentProperties(new: Department): Department {
        return new.setId(uuid())
    }

    private fun setOAProperties(src: Department, oa: Department): Department {
        return src.setOaId(oa.oaId)
            .setOaDeptName(oa.oaDeptName)
            .setOaDeptCode(oa.oaDeptCode)
            .setOaDeleteFlag(oa.oaDeleteFlag)
            .setOaParentId(oa.oaParentId)
            .setOaState(oa.oaState)
            .setOaIsCompany(oa.oaIsCompany)
            .setOaSyncTime(oa.oaSyncTime)
    }

    @Scheduled(cron = "0 30 4 * * ?", every = "3600s")
    @Transactional
    override fun updateAll() {
        val oaDepts = loadAllFromOA()
        val dbDepts = departmentRepo.dao().findAll()
        val new = arrayListOf<Department>()
        val old = arrayListOf<Department>()
        for (oa in oaDepts) {
            val find = dbDepts.find { it.oaId == oa.oaId }
            if (find == null) {
                new.add(setOAProperties(setDefaultNewDepartmentProperties(oa), oa))
                log.info { "prepare to add new department $oa" }
            } else {
                old.add(setOAProperties(find, oa))
            }
        }
        departmentRepo.dao().insert(new)
        departmentRepo.dao().update(old)
        log.info { "updateAll success: old ${old.size} new ${new.size}" }
    }

}
