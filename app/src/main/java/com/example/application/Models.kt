package com.example.application.models

import com.google.gson.annotations.SerializedName

// === Auth ===
data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    val role: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val token: String? = null
)

data class MessageResponse(
    val message: String
)

data class ErrorResponse(
    val message: String
)

data class UserLogin(
    val email: String,
    val password: String
)

data class UserSignup(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val role: String
)

data class CoachSignup(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val role: String
)

data class CoachUser(
    val email: String,
    val password: String,
    val role: String
)

data class SignUpResponse(
    val success: Boolean,
    val message: String
)

// === Profile ===
data class UserProfile(
    val full_name: String,
    val dob: String,
    val weight: Float,
    val gender: String,
    val sport: String,
    val coach_name: String? = null
)

data class CoachProfile(
    val full_name: String,
    val sport: String,
    val email: String,
    val contact: String
)

// === Sensor & Prediction ===
data class SensorData(
    val heart_rate: Float,
    val body_temperature: Float,
    val skin_conductance: Float,
    val ecg_sigmoid: Float
)

data class RawSensorInput(
    val max30105: Map<String, Double>,
    val gy906: Double,
    val groveGsr: Double,
    val ad8232: Int
)

data class SensorDataResponse(
    val success: Boolean,
    val message: String
)

data class HydrationStatusResult(
    val hydration_status: String
)

data class PredictionLogEntry(
    val hydration_status: String,
    val timestamp: String,
    val combined_metrics: Float,
    val heart_rate: Float,
    val body_temperature: Float,
    val skin_conductance: Float,
    val ecg_sigmoid: Float
)

// === Athlete-Coach Linking ===
data class AthleteJoinCoachSchema(
    val coach_name: String
)

// === Session ===
data class SessionMetadata(
    val title: String,
    val activity_type: String,
    val description: String? = null
)

// === Alerts ===
data class Alert(
    val id: String,
    val athlete_id: String,
    val alert_type: String,
    val description: String,
    val timestamp: String,
    val status: String? = null
)

// === Athletes ===
data class Athlete(
    val id: String,
    val name: String,
    val sport: String,
    val hydration_level: Int,
    val heart_rate: Float,
    val body_temp: Float,
    val sweat_rate: Float,
    val status: String
)

// === Settings ===
data class NotificationSettings(
    val hydration_alerts: Boolean = true,
    val sync_notifications: Boolean = true
)

data class UnitSettings(
    val height: String,
    val weight: String,
    val temperature: String
)

// === Dashboard ===
data class CoachDashboard(
    val totalAthletes: Int,
    val criticalHydration: Int,
    val warnings: Int,
    val healthy: Int
)