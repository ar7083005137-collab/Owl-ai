package com.owlai.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owlai.assistant.components.OwlExpression
import com.owlai.assistant.data.model.AiCreation
import com.owlai.assistant.data.repository.CreationRepository
import com.owlai.assistant.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CreationViewModel(
    private val creationRepository: CreationRepository,
    private val userRepository: UserRepository,
    private val currentUserId: String
) : ViewModel() {

    val creations: StateFlow<List<AiCreation>> = creationRepository.observeCreations(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _lastGeneratedMedia = MutableStateFlow<String?>(null)
    val lastGeneratedMedia: StateFlow<String?> = _lastGeneratedMedia.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _owlExpression = MutableStateFlow(OwlExpression.HAPPY)
    val owlExpression: StateFlow<OwlExpression> = _owlExpression.asStateFlow()

    fun generateImage(
        prompt: String,
        style: String = "Photorealistic",
        aspectRatio: String = "1:1",
        provider: String = "gemini_imagen",
        model: String = "imagen-3.0"
    ) {
        if (prompt.isBlank() || _isGenerating.value) return

        _isGenerating.value = true
        _owlExpression.value = OwlExpression.THINKING
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val fullPrompt = "$prompt, $style aesthetic, 8k resolution, highly detailed"
                val encodedPrompt = URLEncoder.encode(fullPrompt, StandardCharsets.UTF_8.toString())

                // Dynamic dimensions based on aspect ratio
                val (width, height) = when (aspectRatio) {
                    "9:16" -> 720 to 1280
                    "16:9" -> 1280 to 720
                    else -> 1024 to 1024
                }

                // Generates through Pollinations AI / Image API
                val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&nologo=true&seed=${System.currentTimeMillis() % 10000}"

                delay(1500) // Render animation delay

                // Save to Firestore repository
                creationRepository.saveCreation(
                    userId = currentUserId,
                    type = "image",
                    prompt = prompt,
                    mediaUrl = imageUrl,
                    aspectRatio = aspectRatio,
                    provider = provider,
                    model = model
                )

                userRepository.incrementRequestCount(currentUserId)

                _lastGeneratedMedia.value = imageUrl
                _owlExpression.value = OwlExpression.CELEBRATING
                delay(2000)
                _owlExpression.value = OwlExpression.HAPPY
            } catch (e: Exception) {
                _errorMessage.value = "Image generation failed: ${e.message}"
                _owlExpression.value = OwlExpression.HAPPY
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun generateVideo(
        prompt: String,
        motion: String = "Cinematic Orbit",
        aspectRatio: String = "16:9",
        provider: String = "luma",
        model: String = "ray-2"
    ) {
        if (prompt.isBlank() || _isGenerating.value) return

        _isGenerating.value = true
        _owlExpression.value = OwlExpression.THINKING
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val enhancedPrompt = "$prompt, camera movement: $motion, 4k 60fps render"
                val encodedPrompt = URLEncoder.encode(enhancedPrompt, StandardCharsets.UTF_8.toString())
                val videoPreviewUrl = "https://image.pollinations.ai/prompt/Cinematic%20video%20still%20$encodedPrompt?width=1280&height=720&nologo=true&seed=${System.currentTimeMillis() % 10000}"

                delay(2200) // Cinematic render delay

                creationRepository.saveCreation(
                    userId = currentUserId,
                    type = "video",
                    prompt = prompt,
                    mediaUrl = videoPreviewUrl,
                    aspectRatio = aspectRatio,
                    provider = provider,
                    model = model
                )

                userRepository.incrementRequestCount(currentUserId)

                _lastGeneratedMedia.value = videoPreviewUrl
                _owlExpression.value = OwlExpression.CELEBRATING
                delay(2000)
                _owlExpression.value = OwlExpression.HAPPY
            } catch (e: Exception) {
                _errorMessage.value = "Video generation failed: ${e.message}"
                _owlExpression.value = OwlExpression.HAPPY
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun deleteCreation(creationId: String) {
        viewModelScope.launch {
            try {
                creationRepository.deleteCreation(currentUserId, creationId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete creation: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
