package com.owlai.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlai.assistant.components.OwlExpression
import com.owlai.assistant.data.model.ChatMessage
import com.owlai.assistant.data.model.ChatSession
import com.owlai.assistant.data.remote.AiService
import com.owlai.assistant.data.repository.ChatRepository
import com.owlai.assistant.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val currentUserId: String
) : ViewModel() {

    val chatSessions: StateFlow<List<ChatSession>> = chatRepository.observeSessions(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _owlExpression = MutableStateFlow(OwlExpression.HAPPY)
    val owlExpression: StateFlow<OwlExpression> = _owlExpression.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun selectChat(chatId: String) {
        _activeChatId.value = chatId
        viewModelScope.launch {
            chatRepository.observeMessages(currentUserId, chatId).collect { msgList ->
                _messages.value = msgList
            }
        }
    }

    fun createNewChat(title: String = "New Owl Conversation", category: String = "general", onCreated: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val newId = chatRepository.createSession(currentUserId, title, category)
                selectChat(newId)
                onCreated(newId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create conversation: ${e.message}"
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteSession(currentUserId, chatId)
                if (_activeChatId.value == chatId) {
                    _activeChatId.value = null
                    _messages.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete chat: ${e.message}"
            }
        }
    }

    fun sendMessage(content: String, currentModel: String = "gemini-2.5-flash") {
        val chatId = _activeChatId.value
        if (chatId == null || content.isBlank() || _isSending.value) return

        _isSending.value = true
        _owlExpression.value = OwlExpression.THINKING
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Send User message to Firestore
                chatRepository.sendMessage(
                    userId = currentUserId,
                    chatId = chatId,
                    role = "user",
                    content = content
                )

                // 2. Prepare conversation history
                val history = _messages.value.map { it.role to it.content }

                // 3. Call AI Service
                val responseText = AiService.generateChatResponse(
                    prompt = content,
                    history = history,
                    model = currentModel
                )

                // 4. Save Assistant response to Firestore
                chatRepository.sendMessage(
                    userId = currentUserId,
                    chatId = chatId,
                    role = "assistant",
                    content = responseText
                )

                // 5. Increment user request metrics
                userRepository.incrementRequestCount(currentUserId)

                _owlExpression.value = OwlExpression.CELEBRATING
                delay(2000)
                _owlExpression.value = OwlExpression.HAPPY
            } catch (e: Exception) {
                _errorMessage.value = "Unable to complete request: ${e.message}"
                _owlExpression.value = OwlExpression.HAPPY
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
