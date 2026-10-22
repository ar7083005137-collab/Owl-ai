package com.owlai.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.owlai.assistant.auth.AuthManager
import com.owlai.assistant.data.model.AppConfig
import com.owlai.assistant.data.model.UserProfile
import com.owlai.assistant.data.repository.ConfigRepository
import com.owlai.assistant.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = AuthManager.authStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val appConfig: StateFlow<AppConfig> = configRepository.observeConfig()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppConfig()
        )

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    val profile = userRepository.syncUserProfile(user)
                    _userProfile.value = profile
                    val adminStatus = configRepository.checkIsAdmin(user.uid, user.email)
                    _isAdmin.value = adminStatus
                } else {
                    _userProfile.value = null
                    _isAdmin.value = false
                }
            }
        }
    }

    fun refreshProfile() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _userProfile.value = userRepository.syncUserProfile(user)
            _isAdmin.value = configRepository.checkIsAdmin(user.uid, user.email)
        }
    }
}
