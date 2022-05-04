package com.auric.submissionaplikasistoryapp.model

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class RegisterUser(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("email")
    var email: String,

    @field:SerializedName("password")
    var password: String,
)