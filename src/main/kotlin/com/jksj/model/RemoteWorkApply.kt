package com.jksj.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class RemoteWorkApply(
    var hour: Double
)
