package com.owlai.assistant.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val role: String = "user",
    val aiRequestsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "general",
    val lastMessage: String = "",
    val messageCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val chatId: String = "",
    val userId: String = "",
    val role: String = "user", // "user" or "assistant"
    val content: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MathSolution(
    @DocumentId val id: String = "",
    val userId: String = "",
    val query: String = "",
    val hasImage: Boolean = false,
    val steps: List<String> = emptyList(),
    val finalAnswer: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AiCreation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "image", // "image" or "video"
    val prompt: String = "",
    val mediaUrl: String = "",
    val aspectRatio: String = "1:1",
    val provider: String = "gemini_imagen",
    val model: String = "imagen-3.0",
    val status: String = "completed", // "completed", "pending", "failed"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AppConfig(
    val version: Long = 1L,
    val maintenanceMode: Boolean = false,
    val activeChatProvider: String = "gemini",
    val chatModel: String = "gemini-2.5-flash",
    val activeImageProvider: String = "gemini_imagen",
    val imageModel: String = "imagen-3.0-generate-002",
    val activeVideoProvider: String = "luma",
    val videoModel: String = "ray-2",
    val chatEnabled: Boolean = true,
    val mathEnabled: Boolean = true,
    val photoMathEnabled: Boolean = true,
    val imageGenEnabled: Boolean = true,
    val videoGenEnabled: Boolean = true,
    val dailyRequestLimit: Long = 50L,
    val updatedBy: String = "system",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AuditLog(
    @DocumentId val id: String = "",
    val adminId: String = "",
    val action: String = "",
    val details: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)
