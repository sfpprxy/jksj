package com.jksj.model

data class Identity(
    val id: String,
    val name: String,
    val token: String,
    val password: String,
    val role: String
)
