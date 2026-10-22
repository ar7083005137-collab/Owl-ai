package com.owlai.assistant.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlai.assistant.components.OwlExpression
import com.owlai.assistant.data.model.MathSolution
import com.owlai.assistant.data.remote.AiService
import com.owlai.assistant.data.repository.MathRepository
import com.owlai.assistant.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MathViewModel(
    private val mathRepository: MathRepository,
    private val userRepository: UserRepository,
    private val currentUserId: String
) : ViewModel() {

    val solutionHistory: StateFlow<List<MathSolution>> = mathRepository.observeMathHistory(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _steps = MutableStateFlow<List<String>>(emptyList())
    val steps: StateFlow<List<String>> = _steps.asStateFlow()

    private val _finalAnswer = MutableStateFlow<String?>(null)
    val finalAnswer: StateFlow<String?> = _finalAnswer.asStateFlow()

    private val _isSolving = MutableStateFlow(false)
    val isSolving: StateFlow<Boolean> = _isSolving.asStateFlow()

    private val _owlExpression = MutableStateFlow(OwlExpression.HAPPY)
    val owlExpression: StateFlow<OwlExpression> = _owlExpression.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateQuery(newQuery: String) {
        _currentQuery.value = newQuery
    }

    fun setSelectedBitmap(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
        if (bitmap != null && _currentQuery.value.isBlank()) {
            _currentQuery.value = "Solve equation from photo"
        }
    }

    fun solve(isPhoto: Boolean = false) {
        val query = _currentQuery.value.ifBlank {
            if (isPhoto) "Photo mathematical problem" else ""
        }
        if (query.isBlank() || _isSolving.value) return

        _isSolving.value = true
        _owlExpression.value = OwlExpression.SOLVING
        _errorMessage.value = null
        _steps.value = emptyList()
        _finalAnswer.value = null

        viewModelScope.launch {
            try {
                val (stepList, answer) = AiService.solveMath(
                    query = query,
                    bitmap = _selectedBitmap.value
                )

                _steps.value = stepList
                _finalAnswer.value = answer

                // Save to Firestore history
                mathRepository.saveMathSolution(
                    userId = currentUserId,
                    query = query,
                    hasImage = isPhoto,
                    steps = stepList,
                    finalAnswer = answer
                )

                userRepository.incrementRequestCount(currentUserId)

                _owlExpression.value = OwlExpression.CELEBRATING
                delay(2500)
                _owlExpression.value = OwlExpression.HAPPY
            } catch (e: Exception) {
                _errorMessage.value = "Could not solve equation: ${e.message}"
                _owlExpression.value = OwlExpression.HAPPY
            } finally {
                _isSolving.value = false
            }
        }
    }

    fun clear() {
        _currentQuery.value = ""
        _selectedBitmap.value = null
        _steps.value = emptyList()
        _finalAnswer.value = null
        _errorMessage.value = null
        _owlExpression.value = OwlExpression.HAPPY
    }
}
