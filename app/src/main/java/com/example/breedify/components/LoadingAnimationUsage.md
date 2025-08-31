# Lottie Loading Animation Usage Guide

## Available Loading Animations

### 1. LoadingAnimation (Full Screen)
```kotlin
LoadingAnimation(
    message = "Loading...",
    modifier = Modifier.fillMaxSize()
)
```

### 2. SmallLoadingAnimation (Compact)
```kotlin
SmallLoadingAnimation(
    size = 100,
    modifier = Modifier.padding(16.dp)
)
```

### 3. DogBreedLoadingAnimation (Dog-themed)
```kotlin
DogBreedLoadingAnimation(
    message = "Analyzing your dog...",
    size = 150
)
```

### 4. PredictionLoadingAnimation (Prediction-specific)
```kotlin
PredictionLoadingAnimation(
    statusMessage = "Processing image...",
    subMessage = "Please wait while we analyze your image...",
    modifier = Modifier.fillMaxWidth()
)
```

## Animation Files
- `dogpawloadinganimation.json` - Your custom dog paw animation
- `loading_animation.json` - Default fallback animation

## Usage in Screens
- **MLPredictionScreen**: Uses `PredictionLoadingAnimation`
- **CameraScreen**: Uses `DogBreedLoadingAnimation` 
- **General Loading**: Use `LoadingAnimation` or `SmallLoadingAnimation`

## Customization
You can easily switch animations by changing the `animationRes` parameter:
```kotlin
LoadingAnimation(
    animationRes = LoadingAnimations.DOG_PAW, // or LoadingAnimations.DEFAULT
    message = "Custom message"
)
```