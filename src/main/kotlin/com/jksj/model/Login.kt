package com.jksj.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class Login(
    var username: String = "",
    var password: String = ""
)
