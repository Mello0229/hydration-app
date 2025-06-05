package com.example.application.models

//import com.example.application.AlertSeverity
import com.example.application.getCurrentDateTime
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
    val name: String,
    val dob: String,
    val weight: Float,
    val gender: String,
    val sport: String,
    val coach_name: String? = null
)

data class CoachProfile(
    val name: String,
    val sport: String,
    val email: String,
    val contact: String
)

// === Sensor & Prediction ===
data class SensorData(
    @SerializedName("heart_rate") val heart_rate: Float,
    @SerializedName("body_temperature") val body_temperature: Float,
    @SerializedName("skin_conductance") val skin_conductance: Float,
    @SerializedName("ecg_sigmoid") val ecg_sigmoid: Float
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

data class HealthStats(
    val heart_rate: String = "N/A",
    val body_temp: String = "N/A",
    val skin_conductance: String = "N/A",
    val hydration_level: String = "N/A",
    val ecg_sigmoid: String = "N/A",
    val last_update: String = getCurrentDateTime()
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
    val alert_type: String,
    val description: String,
    val status: String,
    val timestamp: String,
    val name: String,
    val hydration_level: Float
)

// === Athletes ===
data class Athlete(
    val id: String = "",
    val athlete_id: String = "",
    val name: String = "",
    val email: String = "",
    val sport: String = "",
    val assigned_by: String = "",
    val body_temp: Float = 0f,
    val heart_rate: Float = 0f,
    val hydration_level: Int = 0,
    val status: String = "Hydrated",
//    val sweat_rate: Float = 0f,
    val ecg_sigmoid: Float = 0f,
    val skin_conductance: Float = 0f,
    val alerts: List<Alert> = emptyList()
)

//data class Athlete(
//    val id: String = "",
//    val name: String = "",
//    val sport: String = "",
//    val hydration: Int = 0,
//    val heart_rate: Float? = null,
//    val body_temp: Float? = null,
//    val skin_conductance: Float? = null,
//    val ecg_sigmoid: Float? = null,
//    val status: String = "",
//    val alerts: List<Alert> = emptyList()
//)

data class TrainingSession(
    val title: String,
    val activity_type: String,
    val description: String? = null,
    val sessionTitle: String,
    val date: String,
    val time: String,
    val duration: String,
    val hydrationStart: String,
    val hydrationEnd: String,
    val hydrationPercentStart: Int,
    val hydrationPercentEnd: Int,
    val heartRateStart: Int,
    val heartRateEnd: Int,
    val temperatureStart: Double,
    val temperatureEnd: Double,
    val skinConductanceStart: Double,
    val skinConductanceEnd: Double,
    val ecgStart: Double?,
    val ecgEnd: Double?
    )

data class HydrationAlert(
    val id: String,
    val title: String,
    val message: String,
    val alert_type: AlertType,
    val timestamp: String
)

data class BackendHydrationAlert(
    val alert_type: String,
    val description: String,
    val timestamp: String
)

enum class AlertType {
    CRITICAL,
    WARNING,
    REMINDER
}

// === Dashboard ===
data class CoachDashboard(
    val totalAthletes: Int,
    val criticalHydration: Int,
    val warnings: Int,
    val healthy: Int
)