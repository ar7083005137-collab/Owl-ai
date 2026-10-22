package com.owlai.assistant

import com.owlai.assistant.data.model.AppConfig
import com.owlai.assistant.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OwlAIUnitTest {

    @Test
    fun appConfig_defaultValues_areValid() {
        val config = AppConfig()
        assertEquals(1L, config.version)
        assertFalse(config.maintenanceMode)
        assertTrue(config.chatEnabled)
        assertTrue(config.mathEnabled)
        assertEquals("gemini", config.activeChatProvider)
        assertEquals("gemini-2.5-flash", config.chatModel)
        assertEquals(50L, config.dailyRequestLimit)
    }

    @Test
    fun userProfile_modelCreation_isCorrect() {
        val profile = UserProfile(
            userId = "test_123",
            displayName = "Dr. Owl",
            email = "owl@example.com",
            role = "user"
        )
        assertEquals("test_123", profile.userId)
        assertEquals("Dr. Owl", profile.displayName)
        assertEquals("owl@example.com", profile.email)
        assertEquals("user", profile.role)
        assertEquals(0L, profile.aiRequestsCount)
    }
}
