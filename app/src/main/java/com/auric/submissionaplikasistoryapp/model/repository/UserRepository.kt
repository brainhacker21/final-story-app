package com.auric.submissionaplikasistoryapp.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auric.submissionaplikasistoryapp.model.api.StoryAppApi
import com.auric.submissionaplikasistoryapp.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: StoryAppApi,
) {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean> = _userStatus

    private val _loginData = MutableLiveData<LoginResult>()
    val loginData: LiveData<LoginResult> = _loginData

    fun userLogin(loginUser: LoginUser) {
        _loading.value = true
        val client = apiService.loginUser(loginUser)
        client.enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true

                    Log.d("ResponseApi", "onResponse: ${responseBody.loginResult}")
                    _loginData.value = responseBody.loginResult
                } else {
                    _userStatus.value = false
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                _loading.value = false
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }

    fun userRegister(registerUser: RegisterUser) {
        _loading.value = true
        val client = apiService.registerUser(registerUser)
        client.enqueue(object : Callback<UserRegisterResponse> {
            override fun onResponse(
                call: Call<UserRegisterResponse>,
                response: Response<UserRegisterResponse>
            ) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true

                    Log.d("ResponseAPI", "onResponse: ${responseBody.message}")
                } else {
                    _userStatus.value = false
                }
            }

            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                _loading.value = false
                Log.d("ResponseAPI", "onFailure: ${t.message}")
            }

        })
    }

}