package com.jksj.model

data class OAUser(
    var id: String,
    var deleteFlag: Int,
    var createdId: String,
    var createdBy: String,
    var createdAt: String,
    var modifiedId: String,
    var modifiedBy: String,
    var modifiedAt: String,
    var dataArea: String,
    var loginName: String,
    var realName: String,
    var activitiGroups: String, // this is a typo of OA, not mine
    var departmentId: String,
    var deptName: String,
    var deptCode: Int,
    var password: String,
    var status: Int,
    var syncTime: String,
    var deleteTime: String,
    var dataAreaType: Int,
    var jkyFlag: Int,
    var financeFlag: Int,
    var idCard: String,
    var phone: String
)
