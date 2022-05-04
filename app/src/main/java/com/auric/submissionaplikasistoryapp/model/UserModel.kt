package com.auric.submissionaplikasistoryapp.model

data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val userId: String,
    val login: Boolean
)