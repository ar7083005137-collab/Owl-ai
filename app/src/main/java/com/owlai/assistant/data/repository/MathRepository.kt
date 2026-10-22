package com.owlai.assistant.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.owlai.assistant.R
import com.owlai.assistant.data.model.MathSolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MathRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.applicationContext.getString(R.string.firestore_database_id))
    )

    fun observeMathHistory(userId: String): Flow<List<MathSolution>> {
        val path = "users/$userId/math_solutions"
        return db.collection("users").document(userId).collection("math_solutions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(MathSolution::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    suspend fun saveMathSolution(
        userId: String,
        query: String,
        hasImage: Boolean,
        steps: List<String>,
        finalAnswer: String
    ) {
        val solutionsRef = db.collection("users").document(userId).collection("math_solutions")
        val data = mapOf(
            "userId" to userId,
            "query" to query,
            "hasImage" to hasImage,
            "steps" to steps,
            "finalAnswer" to finalAnswer,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            solutionsRef.document().set(data).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, solutionsRef.path)
            throw e
        }
    }

    suspend fun deleteMathSolution(userId: String, solutionId: String) {
        val docRef = db.collection("users").document(userId).collection("math_solutions").document(solutionId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
