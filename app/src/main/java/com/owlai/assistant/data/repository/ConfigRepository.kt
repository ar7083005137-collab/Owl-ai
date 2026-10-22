package com.owlai.assistant.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.owlai.assistant.R
import com.owlai.assistant.data.model.AppConfig
import com.owlai.assistant.data.model.AuditLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ConfigRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(context.applicationContext.getString(R.string.firestore_database_id))
    )

    fun observeConfig(): Flow<AppConfig> {
        val path = "config/app_config"
        return db.collection("config").document("app_config")
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(AppConfig::class.java) ?: AppConfig()
                } else {
                    AppConfig()
                }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                emit(AppConfig())
            }
    }

    suspend fun updateConfig(adminId: String, adminEmail: String, newConfig: AppConfig, reason: String) {
        val configRef = db.collection("config").document("app_config")
        val auditRef = db.collection("audit_logs").document()

        val nextVersion = newConfig.version + 1L
        val configData = mapOf(
            "version" to nextVersion,
            "maintenanceMode" to newConfig.maintenanceMode,
            "activeChatProvider" to newConfig.activeChatProvider,
            "chatModel" to newConfig.chatModel,
            "activeImageProvider" to newConfig.activeImageProvider,
            "imageModel" to newConfig.imageModel,
            "activeVideoProvider" to newConfig.activeVideoProvider,
            "videoModel" to newConfig.videoModel,
            "chatEnabled" to newConfig.chatEnabled,
            "mathEnabled" to newConfig.mathEnabled,
            "photoMathEnabled" to newConfig.photoMathEnabled,
            "imageGenEnabled" to newConfig.imageGenEnabled,
            "videoGenEnabled" to newConfig.videoGenEnabled,
            "dailyRequestLimit" to newConfig.dailyRequestLimit,
            "updatedBy" to adminEmail,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val auditData = mapOf(
            "adminId" to adminId,
            "action" to "CONFIG_UPDATE",
            "details" to "$reason (v$nextVersion updated by $adminEmail)",
            "timestamp" to FieldValue.serverTimestamp()
        )

        try {
            configRef.set(configData).await()
            auditRef.set(auditData).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, configRef.path)
            throw e
        }
    }

    suspend fun checkIsAdmin(userId: String, email: String?): Boolean {
        if (email.equals("shivambuildwith@gmail.com", ignoreCase = true)) {
            return true
        }
        return try {
            val adminDoc = db.collection("admins").document(userId).get().await()
            adminDoc.exists()
        } catch (e: Exception) {
            false
        }
    }

    fun observeAuditLogs(): Flow<List<AuditLog>> {
        val path = "audit_logs"
        return db.collection("audit_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .snapshots()
            .map { it.toObjects(AuditLog::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }
}
