package com.owlai.assistant.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.owlai.assistant.R
import com.owlai.assistant.data.model.AiCreation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CreationRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.applicationContext.getString(R.string.firestore_database_id))
    )

    fun observeCreations(userId: String): Flow<List<AiCreation>> {
        val path = "users/$userId/creations"
        return db.collection("users").document(userId).collection("creations")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AiCreation::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    suspend fun saveCreation(
        userId: String,
        type: String, // "image" or "video"
        prompt: String,
        mediaUrl: String,
        aspectRatio: String = "1:1",
        provider: String = "gemini_imagen",
        model: String = "imagen-3.0"
    ): String {
        val creationsRef = db.collection("users").document(userId).collection("creations")
        val newDoc = creationsRef.document()
        val data = mapOf(
            "userId" to userId,
            "type" to type,
            "prompt" to prompt,
            "mediaUrl" to mediaUrl,
            "aspectRatio" to aspectRatio,
            "provider" to provider,
            "model" to model,
            "status" to "completed",
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            newDoc.set(data).await()
            return newDoc.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, newDoc.path)
            throw e
        }
    }

    suspend fun deleteCreation(userId: String, creationId: String) {
        val docRef = db.collection("users").document(userId).collection("creations").document(creationId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
