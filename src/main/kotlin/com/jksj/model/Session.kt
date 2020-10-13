package com.jksj.model

import com.jksj.jooq.tables.pojos.AppUser
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class Session(
    var user: AppUser,
    var remoteWork: RemoteWorkStats
)
