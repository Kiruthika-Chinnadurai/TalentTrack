package com.mad.talenttrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.mad.talenttrack.data.model.UserProfile
import com.mad.talenttrack.ui.theme.AccentBlue
import com.mad.talenttrack.ui.theme.CardBackground
import com.mad.talenttrack.ui.theme.DarkBackground
import com.mad.talenttrack.ui.theme.TextSecondary
import com.mad.talenttrack.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onMenuClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val existingProfile by viewModel.userProfile
    val loading by viewModel.loading

    var profileState by remember(existingProfile) {
        mutableStateOf(existingProfile ?: UserProfile())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveProfile(profileState, {}) }) {
                        Text("SAVE", color = AccentBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Profile Image Area
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileState.profilePhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileState.profilePhotoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = profileState.name.ifEmpty { "User Name" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = profileState.email.ifEmpty { "user@email.com" },
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Basic Details Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Basic Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Name (Read-only from Google/Auth)
                        ProfileField("Name", profileState.name, enabled = false) {}
                        
                        // Email (Read-only from Google/Auth)
                        ProfileField("Email", profileState.email, enabled = false) {}
                        
                        // Date of Birth (Editable)
                        ProfileField("Date of Birth", profileState.dateOfBirth, placeholder = "DD/MM/YYYY") {
                            profileState = profileState.copy(dateOfBirth = it)
                        }
                        
                        // Phone Number (Editable)
                        ProfileField("Phone Number", profileState.phone, placeholder = "+91 9876543210") {
                            profileState = profileState.copy(phone = it)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Logout Button
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Logout from Account", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String, 
    value: String, 
    enabled: Boolean = true,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = AccentBlue,
                unfocusedTextColor = if (enabled) Color.White else Color.Gray,
                focusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                disabledBorderColor = Color.DarkGray
            )
        )
    }
}
