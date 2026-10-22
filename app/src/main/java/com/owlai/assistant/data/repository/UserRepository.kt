package com.owlai.assistant.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.owlai.assistant.R
import com.owlai.assistant.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.applicationContext.getString(R.string.firestore_database_id))
    )

    suspend fun syncUserProfile(user: FirebaseUser): UserProfile {
        val userRef = db.collection("users").document(user.uid)
        val snapshot = userRef.get().await()

        val role = if (user.email == "shivambuildwith@gmail.com") "admin" else "user"

        if (!snapshot.exists()) {
            val profile = UserProfile(
                id = user.uid,
                userId = user.uid,
                displayName = user.displayName ?: "Owl Explorer",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString(),
                role = role,
                aiRequestsCount = 0L
            )
            val data = mapOf(
                "userId" to profile.userId,
                "displayName" to profile.displayName,
                "email" to profile.email,
                "photoUrl" to profile.photoUrl,
                "role" to profile.role,
                "aiRequestsCount" to profile.aiRequestsCount,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            try {
                userRef.set(data).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, userRef.path)
            }
            return profile
        } else {
            val existing = snapshot.toObject(UserProfile::class.java)
            val updatedRole = if (user.email == "shivambuildwith@gmail.com") "admin" else (existing?.role ?: "user")
            val updates = mapOf(
                "displayName" to (user.displayName ?: existing?.displayName ?: "Owl Explorer"),
                "photoUrl" to (user.photoUrl?.toString() ?: existing?.photoUrl),
                "role" to updatedRole,
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            try {
                userRef.update(updates).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            }
            return existing?.copy(role = updatedRole) ?: UserProfile(id = user.uid, userId = user.uid, displayName = user.displayName ?: "", email = user.email ?: "", role = updatedRole)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                emit(null)
            }
    }

    suspend fun incrementRequestCount(userId: String) {
        val userRef = db.collection("users").document(userId)
        try {
            userRef.update(
                mapOf(
                    "aiRequestsCount" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, userRef.path)
        }
    }

    fun observeAllUsers(): Flow<List<UserProfile>> {
        val path = "users"
        return db.collection("users")
            .snapshots()
            .map { it.toObjects(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }
}
