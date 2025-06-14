package com.example.application

import android.util.Log
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.application.models.UserLogin
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.application.isInternetAvailable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.application.models.UserSignup

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    LoginScreenView(navController)
}

fun validateCredentials(email: String, password: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    val passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex()

    return email.matches(emailRegex) && password.matches(passwordRegex)
}

@Composable
fun LoginScreenView(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val sharedViewModel: SharedViewModel = viewModel()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.wave_border),
                contentDescription = "Wave Design",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontSize = 14.sp, color = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontSize = 14.sp, color = Color.Gray) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            Text(
                text = "Forgot Your Password?",
                fontSize = 13.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (!isInternetAvailable(context)) {
                            errorMessage = "No internet connection."
                            return@Button
                        }
                        if (validateCredentials(email, password)) {
                            scope.launch {
                                try {
                                    Log.i("ATHLETE_LOGIN", "Username:["+ email+"]")
                                    Log.i("ATHLETE_LOGIN", "Password:["+ password+"]")
                                    val userSignup = UserLogin(
                                        email = email,
                                        password = password
                                    )
                                    val response = RetrofitInstance.authApi.login(userSignup)
                                    Log.i("ATHLETE_LOGIN", "response: "+ response)
                                    if (response.accessToken != null) {
                                        RetrofitInstance.authToken = response.accessToken
                                        sharedViewModel.setAuthToken(response.accessToken)
                                        Log.d("LOGIN_SUCCESS", "Token set in SharedViewModel: ${response.accessToken}")
                                        navController.navigate("home")

                                    } else {
                                        errorMessage = "This account is not registered as Athlete"
                                    }
//                                    if (response.accessToken != null) {
//                                        RetrofitInstance.authToken = response.accessToken
//                                        navController.navigate("home")
//                                    } else {
//                                        errorMessage = "Login failed"
//                                    }

                                } catch (e: Exception) {
                                    Log.e("ATHLETE_LOGIN_ERROR", "Error during login: ${e.localizedMessage}")
                                    errorMessage = when (e) {
                                        is java.net.UnknownHostException -> "No internet connection."
                                        is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                                        else -> "This account is not registered as Athlete"
                                    }
                                }
                            }
                        } else {
                            errorMessage = "Invalid email or password format."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
                    modifier = Modifier
                        .width(140.dp)
                        .height(36.dp)
                ) {
                    Text(text = "Login as Athlete", color = Color.Black, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        if (!isInternetAvailable(context)) {
                            errorMessage = "No internet connection."
                            return@Button
                        }
                        if (validateCredentials(email, password)) {
                            scope.launch {
                                Log.i("COACH_LOGIN", "Username:[" + email + "]")
                                Log.i("COACH_LOGIN", "Password:[" + password + "]")
                                try {
                                    val userSignup = UserLogin(
                                        email = email,
                                        password = password
                                    )
                                    val response = RetrofitInstance.authApi.coachLogin(userSignup)
                                    Log.i("COACH_LOGIN", "response: " + response)
                                    if (response.accessToken != null) {
                                        RetrofitInstance.authToken = response.accessToken
                                        sharedViewModel.setCoachEmail(email)
                                        sharedViewModel.setAuthToken(response.accessToken)
//                                        sharedViewModel.fetchAthletes()
                                        Log.d("LOGIN_SUCCESS", "Token set in SharedViewModel: ${response.accessToken}")
                                        navController.navigate("coachHomeScreen")
                                    } else {
                                        errorMessage = "This account is not registered as Coach"
                                    }
//                                    if (response.accessToken != null) {
//                                        RetrofitInstance.authToken = response.accessToken
//                                        navController.navigate("coachHomeScreen")
//                                    } else {
//                                        errorMessage = "Login failed"
//                                    }
                                } catch (e: Exception) {
                                    Log.e("COACH_LOGIN_ERROR", "Error during login: ${e.localizedMessage}")
                                    errorMessage = when (e) {
                                        is java.net.UnknownHostException -> "No internet connection."
                                        is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                                        else -> "This account is not registered as Coach"
                                    }
                                }
                            }
                        } else {
                            errorMessage = "Invalid email or password format."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
                    modifier = Modifier
                        .width(140.dp)
                        .height(36.dp)
                ) {
                    Text(text = "Login as Coach", color = Color.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate("home")
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(text = "Go to Athlete", color = Color.Black, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        navController.navigate("coachHomeScreen")
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(text = "Go to Coach", color = Color.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 22.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Don't have an account? ", color = Color.Black, fontSize = 13.sp)
                Text(
                    text = "Sign-Up",
                    color = Color(0xFF0496E5),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate("signup") }
                        .padding(start = 3.dp),
                )
            }
        }
    }
}