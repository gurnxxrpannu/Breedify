package com.example.breedify.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraUtils {
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        
        fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, "Breedify").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        }
        
        fun createImageFile(context: Context): File {
            val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
            val outputDirectory = getOutputDirectory(context)
            return File(outputDirectory, "IMG_${timestamp}.jpg")
        }
        
        fun captureImage(
            imageCapture: ImageCapture,
            outputFile: File,
            context: Context,
            onImageCaptured: (Uri) -> Unit,
            onError: (ImageCaptureException) -> Unit
        ) {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
            
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        onImageCaptured(Uri.fromFile(outputFile))
                    }
                    
                    override fun onError(exception: ImageCaptureException) {
                        onError(exception)
                    }
                }
            )
        }
        
        fun processImageForML(imageUri: Uri, context: Context): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                // Resize image for ML processing (typically 224x224 or 299x299)
                resizeBitmap(bitmap, 224, 224)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            val scaleWidth = width.toFloat() / bitmap.width
            val scaleHeight = height.toFloat() / bitmap.height
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        }
        
        fun saveProcessedImage(bitmap: Bitmap, context: Context): Uri? {
            return try {
                val file = createImageFile(context)
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
