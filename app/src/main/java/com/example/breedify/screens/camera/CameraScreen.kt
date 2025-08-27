package com.example.breedify.screens.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.breedify.utils.CameraUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            val provider = ProcessCameraProvider.getInstance(context).get()
            cameraProvider = provider
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    
    when {
        cameraPermissionState.status.isGranted -> {
            CameraContent(
                context = context,
                lifecycleOwner = lifecycleOwner,
                lensFacing = lensFacing,
                onLensFacingChanged = { lensFacing = it },
                onImageCaptured = onImageCaptured,
                onBackPressed = onBackPressed,
                cameraProvider = cameraProvider,
                onImageCaptureReady = { imageCapture = it }
            )
        }
        cameraPermissionState.status.shouldShowRationale -> {
            PermissionRationaleContent(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                onBackPressed = onBackPressed
            )
        }
        else -> {
            PermissionRequestContent(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                onBackPressed = onBackPressed
            )
        }
    }
}

@Composable
private fun CameraContent(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    lensFacing: Int,
    onLensFacingChanged: (Int) -> Unit,
    onImageCaptured: (Uri) -> Unit,
    onBackPressed: () -> Unit,
    cameraProvider: ProcessCameraProvider?,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx)
            },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            cameraProvider?.let { provider ->
                val preview = Preview.Builder().build()
                val imageCaptureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                
                imageCapture = imageCaptureUseCase
                onImageCaptureReady(imageCaptureUseCase)
                
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()
                
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCaptureUseCase
                    )
                    preview.setSurfaceProvider(view.surfaceProvider)
                } catch (exc: Exception) {
                    Toast.makeText(context, "Camera initialization failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Camera controls overlay
        CameraControls(
            onCaptureClick = {
                imageCapture?.let { capture ->
                    val outputFile = CameraUtils.createImageFile(context)
                    
                    CameraUtils.captureImage(
                        imageCapture = capture,
                        outputFile = outputFile,
                        context = context,
                        onImageCaptured = onImageCaptured,
                        onError = { exception ->
                            Toast.makeText(
                                context,
                                "Photo capture failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            },
            onFlipCamera = {
                onLensFacingChanged(
                    if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                )
            },
            onBackPressed = onBackPressed,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CameraControls(
    onCaptureClick: () -> Unit,
    onFlipCamera: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Top controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = onFlipCamera,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Text(
                    text = "🔄",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Capture button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = onCaptureClick,
                modifier = Modifier.size(72.dp),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Text(
                    text = "📷",
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequestPermission: () -> Unit,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📷",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "To identify dog breeds, we need access to your camera to take photos.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Camera Permission")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = onBackPressed) {
            Text("Go Back")
        }
    }
}

@Composable
private fun PermissionRationaleContent(
    onRequestPermission: () -> Unit,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🐕",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Camera Access Needed",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Breedify uses your camera to capture photos of dogs for breed identification. This helps our ML model analyze and classify different dog breeds accurately.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Allow Camera Access")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = onBackPressed) {
            Text("Maybe Later")
        }
    }
}
