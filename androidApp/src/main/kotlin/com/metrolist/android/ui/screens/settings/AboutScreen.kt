package com.metrolist.android.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.metrolist.android.LocalNavigator
import com.metrolist.android.ui.AshuCustomizationDialog
import com.metrolist.android.ui.AshuPrefs
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    var showCustomize by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val dpPath = remember(refreshKey) { AshuPrefs.getImage(context, "dp") }
    val userName = remember(refreshKey) { AshuPrefs.getName(context) }

    if (showCustomize) {
        AshuCustomizationDialog(
            onDismiss = { showCustomize = false },
            onUpdated = { refreshKey++ }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About AshuX Music") },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (dpPath != null) {
                AsyncImage(
                    model = File(dpPath),
                    contentDescription = "Profile DP",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(110.dp).clip(CircleShape).border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(110.dp).clip(CircleShape).border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("A", fontSize = 44.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("AshuX Music", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Developed & Customized by Ashutosh Kumar", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "I'm Ashutosh Kumar. ✨ 💻 I build apps and dream beyond limits. 🚀 Focused on my goals, not on competing with others. 🧠 I believe intelligence is attractive, kindness is powerful, and loyalty is rare. 🥂 I enjoy meaningful conversations over meaningless attention. 🖤 Still growing, still learning, still becoming the best version of myself.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Made in भारत 🇮🇳", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showCustomize = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Customize Welcome Note, DP & Wallpapers")
            }
        }
    }
}
