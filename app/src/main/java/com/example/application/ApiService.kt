//package com.example.application.network
//
//import retrofit2.http.*
//import com.example.application.models.*
//import com.example.application.models.TokenResponse
//import com.example.application.models.CoachUser
//import com.example.application.models.MessageResponse
//import com.example.application.models.Alert
//import com.example.application.models.Athlete
//import com.example.application.models.CoachDashboard
//import com.example.application.models.UserSignup
//
//interface ApiService {
//
//    // === Athlete Auth ===
//    @FormUrlEncoded
//    @POST("/auth/login")
//    suspend fun login(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): TokenResponse
//
//    @POST("/auth/signup")
//    suspend fun signup(@Body user: UserSignup): MessageResponse
//
//    // === Coach Auth ===
//    @POST("/coach/auth/login")
//    suspend fun coachLogin(@Body login: UserLogin): TokenResponse
//
//    @POST("/coach/auth/signup")
//    suspend fun coachSignup(@Body user: CoachUser): MessageResponse
//
//    // === Alerts ===
//    @GET("/alerts")
//    suspend fun getAlerts(): List<Alert>
//
//    @GET("/alerts/{athlete_id}")
//    suspend fun getAlertsByAthlete(@Path("athlete_id") id: String): List<Alert>
//
//    // === Athletes ===
//    @GET("/athletes")
//    suspend fun getAthletes(): List<Athlete>
//
//    @POST("/athletes/add")
//    suspend fun addAthlete(@Body athlete: Athlete): MessageResponse
//
//    @DELETE("/athletes/remove/{id}")
//    suspend fun removeAthlete(@Path("id") id: String): MessageResponse
//
//    // === Dashboard ===
//    @GET("/dashboard")
//    suspend fun getDashboardStats(): CoachDashboard
//}