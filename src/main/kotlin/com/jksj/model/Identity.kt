package com.jksj.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class Identity(
    var token: String
)
