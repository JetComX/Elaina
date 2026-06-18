package com.jetcomx.elaina.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_WELCOMED = "welcomed"
    }

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _showWelcome = MutableStateFlow(!prefs.getBoolean(KEY_WELCOMED, false))
    val showWelcome: StateFlow<Boolean> = _showWelcome

    fun dismissWelcome() {
        viewModelScope.launch {
            prefs.edit().putBoolean(KEY_WELCOMED, true).apply()
            _showWelcome.value = false
        }
    }
}