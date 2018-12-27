package com.projects.valerian.vocabularylist.models

data class User(
    val id: String,
    val username: String,
    val email: String?,
    val bearerToken: String,
    val bearerExpiry: Long
)
