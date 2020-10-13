package com.jksj.model

data class OAResp<T>(
    var success: String,
    var msg: String,
    var data: List<T>,
    var login: Boolean
)
