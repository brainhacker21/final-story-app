package com.auric.submissionaplikasistoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.auric.submissionaplikasistoryapp.model.LoginResult
import com.auric.submissionaplikasistoryapp.model.LoginUser
import com.auric.submissionaplikasistoryapp.model.RegisterUser
import com.auric.submissionaplikasistoryapp.model.UserPreference
import com.auric.submissionaplikasistoryapp.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: UserPreference
) : ViewModel() {

    val isLoading: LiveData<Boolean> = userRepository.loading
    val userStatus: LiveData<Boolean> = userRepository.userStatus
    val loginResult: LiveData<LoginResult> = userRepository.loginData

    fun userLogin(loginUser: LoginUser) {
        userRepository.userLogin(loginUser)
    }

    fun userRegister(registerUser: RegisterUser) {
        userRepository.userRegister(registerUser)
    }

    fun saveUserPreference(loginResult: LoginResult) {
        viewModelScope.launch {
            preference.saveUserData(loginResult)
        }
    }

    fun getUserPreferences(): LiveData<LoginResult> {
        return preference.getUserData().asLiveData()
    }

    fun clearUserPreference() {
        viewModelScope.launch {
            preference.clearUserData()
        }
    }
}