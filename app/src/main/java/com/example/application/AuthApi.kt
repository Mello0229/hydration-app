package com.example.application.network

import com.example.application.models.Alert
import com.example.application.models.Athlete
import com.example.application.Screen
import com.example.application.models.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    // === Athlete Auth ===
    //@FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Body login: UserLogin
//        @Field("email") email: String,
//        @Field("password") password: String
    ): TokenResponse

    @POST("/auth/signup")
    suspend fun signup(@Body user: UserSignup): TokenResponse

    // === Coach Auth ===
    @POST("/coach/auth/login")
    suspend fun coachLogin(@Body login: UserLogin): TokenResponse

    @POST("/coach/auth/signup")
    suspend fun coachSignup(@Body user: CoachSignup): TokenResponse

    // === User Profile ===
    @GET("/user/profile")
    suspend fun getProfile(): UserProfile

    @POST("/user/profile")
    suspend fun updateProfile(@Body profile: UserProfile): UserProfile

    @POST("/profile/")
    suspend fun updateCoachProfile(@Body profile: CoachProfile): CoachProfile

    // === Alerts ===
    @POST("alerts/hydration")
    suspend fun HydrationAlert(@Query("hydration_level") hydrationLevel: Int): Response<Unit>

    @GET("/alerts")
    suspend fun getBackendHydrationAlert(): List<BackendHydrationAlert>

//    @GET("/alerts")
//    suspend fun getAlerts(): List<Alert>

    @GET("/alerts/{athlete_id}")
    suspend fun getAlertsByAthlete(@Path("athlete_id") id: String): List<Alert>

    @GET("/coach/alerts/")
    suspend fun getAllCoachAlerts(): List<Alert>

    // === Athletes ===
    @GET("/athletes/")
    suspend fun getAthletes(): List<Athlete>
//    @GET("/athletes/")
//    suspend fun getAthletes(@Header("Authorization") token: String): List<Athlete>

    // ===Sensor Data ===
    @POST("/data/receive")
    suspend fun postSensorData(@Body data: List<SensorData>): SensorDataResponse

    // === Dashboard ===
    @GET("/dashboard")
    suspend fun getDashboardStats(): CoachDashboard
}