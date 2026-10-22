package com.owlai.assistant.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import org.json.JSONObject

enum class OperationType {
    GET,
    LIST,
    CREATE,
    UPDATE,
    DELETE
}

fun handleFirestoreError(e: Exception, operation: OperationType, path: String) {
    val errorJson = JSONObject()
    errorJson.put("error", e.localizedMessage ?: e.message ?: "Unknown error")
    errorJson.put("operationType", operation.name.lowercase())
    errorJson.put("path", path)
    if (e is FirebaseFirestoreException) {
        errorJson.put("code", e.code.name)
    }
    Log.e("FirestoreError", errorJson.toString())
}
