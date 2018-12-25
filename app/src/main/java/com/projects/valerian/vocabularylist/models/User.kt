package com.projects.valerian.vocabularylist.models

import java.util.*

data class User(
    val id: String,
    val username: String,
    val email: String?,
    val bearerToken: String
)
