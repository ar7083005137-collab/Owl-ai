package com.owlai.assistant.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.owlai.assistant.R
import com.owlai.assistant.data.model.ChatMessage
import com.owlai.assistant.data.model.ChatSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ChatRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.applicationContext.getString(R.string.firestore_database_id))
    )

    fun observeSessions(userId: String): Flow<List<ChatSession>> {
        val path = "users/$userId/chats"
        return db.collection("users").document(userId).collection("chats")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(ChatSession::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    suspend fun createSession(userId: String, title: String, category: String = "general"): String {
        val chatsRef = db.collection("users").document(userId).collection("chats")
        val newDoc = chatsRef.document()
        val data = mapOf(
            "userId" to userId,
            "title" to title,
            "category" to category,
            "lastMessage" to "",
            "messageCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            newDoc.set(data).await()
            return newDoc.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, newDoc.path)
            throw e
        }
    }

    suspend fun deleteSession(userId: String, chatId: String) {
        val chatDoc = db.collection("users").document(userId).collection("chats").document(chatId)
        try {
            chatDoc.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, chatDoc.path)
            throw e
        }
    }

    fun observeMessages(userId: String, chatId: String): Flow<List<ChatMessage>> {
        val path = "users/$userId/chats/$chatId/messages"
        return db.collection("users").document(userId).collection("chats").document(chatId).collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(ChatMessage::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    suspend fun sendMessage(
        userId: String,
        chatId: String,
        role: String,
        content: String,
        imageUrl: String? = null
    ) {
        val messagesRef = db.collection("users").document(userId).collection("chats").document(chatId).collection("messages")
        val chatDocRef = db.collection("users").document(userId).collection("chats").document(chatId)

        val messageData = mapOf(
            "chatId" to chatId,
            "userId" to userId,
            "role" to role,
            "content" to content,
            "imageUrl" to imageUrl,
            "createdAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        try {
            messagesRef.document().set(messageData).await()
            // Update parent chat summary
            chatDocRef.update(
                mapOf(
                    "lastMessage" to content.take(150),
                    "messageCount" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, messagesRef.path)
            throw e
        }
    }
}
