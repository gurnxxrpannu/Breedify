# Breedify ProGuard Rules for Production

# Keep source file names and line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep data classes and API models
-keep class com.example.breedify.data.api.** { *; }
-keep class com.example.breedify.data.model.** { *; }

# Keep Retrofit and Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Keep TensorFlow Lite classes
-keep class org.tensorflow.lite.** { *; }

# Keep Lottie animation classes
-keep class com.airbnb.lottie.** { *; }

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Remove debug logging from custom Logger
-assumenosideeffects class com.example.breedify.utils.Logger {
    public static *** d(...);
    public static *** i(...);
    public static *** v(...);
}