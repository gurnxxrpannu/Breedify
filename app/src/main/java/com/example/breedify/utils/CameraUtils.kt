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
                
                // Resize image for ML processing to 299x299 for TensorFlow Lite model
                resizeBitmap(bitmap, 299, 299)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        /**
         * Processes an image for the Gemini API, maintaining aspect ratio while resizing to a maximum dimension.
         * @param imageUri The URI of the image to process
         * @param context The application context
         * @param maxDimension The maximum dimension (width or height) for the processed image
         * @return The processed Bitmap or null if processing fails
         */
        fun processImageForGemini(imageUri: Uri, context: Context, maxDimension: Int = 1024): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()
                
                // Calculate inSampleSize to load a smaller version of the image
                options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
                options.inJustDecodeBounds = false
                
                // Reload the image with the calculated sample size
                val newInputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
                newInputStream?.close()
                
                // Resize to maintain aspect ratio with max dimension
                bitmap?.let { resizeToMaxDimension(it, maxDimension) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
        
        private fun resizeToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            
            // If already smaller than max dimension, return as is
            if (width <= maxDimension && height <= maxDimension) {
                return bitmap
            }
            
            val scale: Float = if (width > height) {
                maxDimension.toFloat() / width
            } else {
                maxDimension.toFloat() / height
            }
            
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
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
        
        fun copyFileToBreedifyDirectory(sourceUri: Uri, context: Context): Uri? {
            return try {
                val inputStream = context.contentResolver.openInputStream(sourceUri)
                if (inputStream != null) {
                    val destinationFile = createImageFile(context)
                    val outputStream = FileOutputStream(destinationFile)
                    
                    inputStream.copyTo(outputStream)
                    
                    inputStream.close()
                    outputStream.flush()
                    outputStream.close()
                    
                    Uri.fromFile(destinationFile)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
