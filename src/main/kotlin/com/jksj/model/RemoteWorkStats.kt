package com.jksj.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class RemoteWorkStats(
    var link: String = "",
    var timeStart: Long? = null,
    var timeApplied: Long? = null,
    var timeRemaining: Long = 0,
    var timeUsed: Int = 0,
    var timeUsedSec: Long = 0
)
