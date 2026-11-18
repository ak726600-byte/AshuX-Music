package com.metrolist.android.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import java.io.File

object AshuPrefs {
    private const val PREFS = "ashux_custom_prefs"
    fun getName(ctx: Context): String = ctx.getSharedPreferences(PREFS, 0).getString("user_name", "Ashutosh Kumar") ?: "Ashutosh Kumar"
    fun setName(ctx: Context, name: String) = ctx.getSharedPreferences(PREFS, 0).edit().putString("user_name", name).apply()

    fun saveImage(ctx: Context, uri: Uri, type: String): String {
        val file = File(ctx.filesDir, "ashux_$type.jpg")
        ctx.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        ctx.getSharedPreferences(PREFS, 0).edit().putString(type, file.absolutePath).apply()
        return file.absolutePath
    }

    fun getImage(ctx: Context, type: String): String? {
        val path = ctx.getSharedPreferences(PREFS, 0).getString(type, null)
        return if (path != null && File(path).exists()) path else null
    }

    fun clearImage(ctx: Context, type: String) {
        ctx.getSharedPreferences(PREFS, 0).edit().remove(type).apply()
    }
}

@Composable
fun AshuWelcomeOverlay(onFinished: () -> Unit) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(true) }
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val userName = remember { AshuPrefs.getName(context) }
    val dpPath = remember { AshuPrefs.getImage(context, "dp") }
    val splashBg = remember { AshuPrefs.getImage(context, "splash_bg") }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(700))
        scale.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        delay(1800)
        visible = false
        delay(500)
        onFinished()
    }

    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut(tween(500))) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0F)),
            contentAlignment = Alignment.Center
        ) {
            if (splashBg != null) {
                AsyncImage(
                    model = File(splashBg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.45f)
                )
            }
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000)))
                )
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(scale.value).alpha(alpha.value).padding(24.dp)
            ) {
                if (dpPath != null) {
                    AsyncImage(
                        model = File(dpPath),
                        contentDescription = "DP",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(120.dp).clip(CircleShape).border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(120.dp).clip(CircleShape).border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(userName.take(1).uppercase(), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "AshuX Music",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AshuCustomizationDialog(onDismiss: () -> Unit, onUpdated: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(AshuPrefs.getName(context)) }

    val dpPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { AshuPrefs.saveImage(context, it, "dp"); onUpdated() }
    }
    val splashPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { AshuPrefs.saveImage(context, it, "splash_bg"); onUpdated() }
    }
    val playerPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { AshuPrefs.saveImage(context, it, "player_bg"); onUpdated() }
    }
    val searchPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { AshuPrefs.saveImage(context, it, "search_bg"); onUpdated() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AshuX Music Customization", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; AshuPrefs.setName(context, it) },
                    label = { Text("Your Welcome Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { dpPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set Profile DP (Welcome Note)")
                }
                Button(onClick = { splashPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set Starting Animation Wallpaper")
                }
                Button(onClick = { playerPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set Music Player Wallpaper")
                }
                Button(onClick = { searchPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set Search Screen Wallpaper")
                }
                TextButton(
                    onClick = {
                        AshuPrefs.clearImage(context, "splash_bg")
                        AshuPrefs.clearImage(context, "player_bg")
                        AshuPrefs.clearImage(context, "search_bg")
                        onUpdated()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset All Wallpapers")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onUpdated(); onDismiss() }) { Text("Done") }
        }
    )
}
