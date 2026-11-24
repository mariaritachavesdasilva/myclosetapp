package com.example.myclosetapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _forgotPasswordMessage = MutableStateFlow<String?>(null)
    val forgotPasswordMessage: StateFlow<String?> = _forgotPasswordMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onNameChange(value: String) {
        _name.value = value
        _errorMessage.value = null
    }

    fun onEmailChange(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleMode() {
        _isSignUpMode.value = !_isSignUpMode.value
        _errorMessage.value = null
        _forgotPasswordMessage.value = null
    }

    fun onForgotPasswordClick() {
        if (_email.value.isBlank()) {
            _errorMessage.value = "Digite seu e-mail no campo acima para redefinir a senha."
        } else {
            _forgotPasswordMessage.value = "Um link de redefinição simulado foi enviado para ${_email.value.trim()}."
        }
    }

    fun dismissForgotPasswordDialog() {
        _forgotPasswordMessage.value = null
    }

    fun onSubmit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = if (_isSignUpMode.value) {
                authRepository.signup(_name.value, _email.value, _password.value)
            } else {
                authRepository.login(_email.value, _password.value)
            }

            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { ex ->
                _errorMessage.value = ex.message ?: "Ocorreu um erro. Tente novamente."
            }
        }
    }

    fun onContinueAsVisitor(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.continueAsVisitor()
            _isLoading.value = false
            onSuccess()
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(authRepository) as T
        }
    }
}
