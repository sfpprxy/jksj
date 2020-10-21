package com.jksj.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class UserInfo(
    var id: String,
    var account: String,
    var name: String,
    var department: String
)
