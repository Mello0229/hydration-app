package com.example.application

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.application.models.CoachSignup
import com.example.application.models.CoachUser
import com.example.application.models.ErrorResponse
import com.example.application.models.SignUpResponse
import com.example.application.models.UserSignup
import com.example.application.network.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var roleError by remember { mutableStateOf<String?>(null) }
    var signupError by remember { mutableStateOf<String?>(null) }

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    val passwordRegex = "^(?=.*[A-Z])(?=.*\\d)[^\\s]{6,}$".toRegex()
    val roleOptions = listOf("athlete", "coach")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Image(
            painter = painterResource(id = R.drawable.wave_border),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign-Up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = emailError != null,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE3E3E3),
                        unfocusedContainerColor = Color(0xFFE3E3E3),
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.Black
                    )
                )
                emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        isError = firstNameError != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE3E3E3),
                            unfocusedContainerColor = Color(0xFFE3E3E3),
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        isError = lastNameError != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE3E3E3),
                            unfocusedContainerColor = Color(0xFFE3E3E3),
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                }
                firstNameError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                lastNameError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                Spacer(modifier = Modifier.height(10.dp))

                var passwordVisible by remember { mutableStateOf(false) }
                var confirmPasswordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = password,
                    onValueChange = { if (!it.contains(" ")) password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE3E3E3),
                        unfocusedContainerColor = Color(0xFFE3E3E3),
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.Black
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { if (!it.contains(" ")) confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE3E3E3),
                        unfocusedContainerColor = Color(0xFFE3E3E3),
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.Black
                    )
                )
                confirmPasswordError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {},
                        label = { Text("Role (Athlete/Coach)") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE3E3E3),
                            unfocusedContainerColor = Color(0xFFE3E3E3),
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                    ) {
                        roleOptions.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                roleError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }

                signupError?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        emailError = null
                        firstNameError = null
                        lastNameError = null
                        passwordError = null
                        confirmPasswordError = null
                        roleError = null
                        signupError = null

                        if (email.isBlank() || !email.matches(emailRegex)) {
                            emailError = "Invalid email format"
                        }
                        if (firstName.isBlank()) {
                            firstNameError = "First name is required"
                        }
                        if (lastName.isBlank()) {
                            lastNameError = "Last name is required"
                        }
                        if (password.isBlank() || !password.matches(passwordRegex)) {
                            passwordError = "Password must be at least 6 characters, contain 1 uppercase letter & 1 number, and have no spaces"
                        }
                        if (confirmPassword != password) {
                            confirmPasswordError = "Passwords do not match"
                        }
                        if (selectedRole.isBlank()) {
                            roleError = "Please select a role"
                        }

                        if (emailError == null && firstNameError == null && lastNameError == null &&
                            passwordError == null && confirmPasswordError == null && roleError == null
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val userSignup = UserSignup(
                                        first_name = firstName,
                                        last_name = lastName,
                                        email = email,
                                        password = password,
                                        confirm_password = confirmPassword,
                                        role = selectedRole.lowercase()
                                    )
                                    Log.d("SIGNUP_PAYLOAD", Gson().toJson(userSignup))
                                    if (selectedRole == "athlete") {
                                        val response = RetrofitInstance.authApi.signup(userSignup)
                                        withContext(Dispatchers.Main) {
                                            Log.d("response", "$response")
                                            withContext(Dispatchers.Main) {
                                                Log.d("response", "$response")
                                                if (response != null) {
                                                    RetrofitInstance.authToken =
                                                        response.accessToken
                                                    Log.d("SIGNUP_PAYLOAD", response.accessToken)
                                                    navController.navigate("createProfile/$selectedRole")
                                                } else {
                                                    Log.e("SIGNUP_ERROR_BODY", "No error body")
                                                    signupError = "Signup failed"
                                                }
                                            }
                                        }
                                    } else {
                                        val coachSignup = CoachSignup(
                                            first_name = firstName,
                                            last_name = lastName,
                                            email = email,
                                            password = password,
                                            confirm_password = confirmPassword,
                                            role = selectedRole.lowercase()
                                        )
                                        val response = RetrofitInstance.authApi.coachSignup(coachSignup)
                                        Log.d("SIGNUP_PAYLOAD", "$response")
                                        withContext(Dispatchers.Main) {
                                            Log.d("response", "$response")
                                            if (response != null) {
                                                RetrofitInstance.authToken = response.accessToken
                                                Log.d("SIGNUP_PAYLOAD", response.accessToken)
                                                navController.navigate("createProfile/$selectedRole")
                                            } else {
                                                Log.e("SIGNUP_ERROR_BODY", "No error body")
                                                signupError = "Signup failed"
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Log.e("SIGNUP_ERROR", "Exception: ", e)
                                        signupError = e.localizedMessage ?: "Signup failed"
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Create Account", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Already have an account? ", color = Color.Black, fontSize = 14.sp)
                Text(
                    text = "Login",
                    color = Color(0xFF0496E5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}