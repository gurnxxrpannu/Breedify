# Deployment Guide

This guide covers how to prepare and deploy Breedify for production.

## Pre-Deployment Checklist

### 1. API Keys Configuration
Ensure all API keys are properly configured in `local.properties`:

```properties
# Required API Keys
HUGGINGFACE_API_KEY=hf_your_actual_token_here
GEMINI_API_KEY=your_actual_gemini_key_here
DOG_API_KEY=your_actual_dog_api_key_here
```

### 2. Version Management
Update version information in `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 1  // Increment for each release
    versionName = "1.0.0"  // Follow semantic versioning
}
```

### 3. Build Configuration
Verify release build configuration:

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        isDebuggable = false
    }
}
```

## Production Build

### 1. Clean Build
```bash
./gradlew clean
```

### 2. Generate Release APK
```bash
./gradlew assembleRelease
```

### 3. Generate Release AAB (for Play Store)
```bash
./gradlew bundleRelease
```

### 4. Build Outputs
- **APK**: `app/build/outputs/apk/release/app-release.apk`
- **AAB**: `app/build/outputs/bundle/release/app-release.aab`

## App Signing

### 1. Generate Keystore (First Time Only)
```bash
keytool -genkey -v -keystore breedify-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias breedify
```

### 2. Configure Signing in build.gradle.kts
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("path/to/breedify-release-key.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = "breedify"
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        // ... other config
    }
}
```

### 3. Environment Variables
Set these environment variables before building:
```bash
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_PASSWORD=your_key_password
```

## Security Checklist

### 1. API Keys
- ✅ All API keys stored in `local.properties`
- ✅ No hardcoded keys in source code
- ✅ `local.properties` in `.gitignore`

### 2. ProGuard/R8
- ✅ Code obfuscation enabled
- ✅ Resource shrinking enabled
- ✅ Debug logging removed in release builds

### 3. Permissions
- ✅ Only necessary permissions requested
- ✅ Runtime permissions properly handled
- ✅ Permission rationale provided to users

## Performance Optimization

### 1. APK Size
- ✅ Resource shrinking enabled
- ✅ Unused resources removed
- ✅ Vector drawables used where possible
- ✅ Image compression optimized

### 2. Runtime Performance
- ✅ Memory leaks checked
- ✅ Network requests optimized
- ✅ Image loading optimized with Coil
- ✅ Proper lifecycle management

## Testing Before Release

### 1. Manual Testing
- [ ] Test on multiple device sizes
- [ ] Test on different Android versions (API 24+)
- [ ] Test all user flows
- [ ] Test network error scenarios
- [ ] Test permission flows

### 2. Performance Testing
- [ ] Memory usage profiling
- [ ] Network usage monitoring
- [ ] Battery usage testing
- [ ] App startup time measurement

### 3. Security Testing
- [ ] API key security verification
- [ ] Network traffic analysis
- [ ] App permissions audit

## Google Play Store Deployment

### 1. Play Console Setup
1. Create app in Google Play Console
2. Complete store listing
3. Set up content rating
4. Configure pricing and distribution

### 2. Upload Build
1. Upload AAB file to Play Console
2. Complete release notes
3. Set rollout percentage (start with 5-10%)
4. Submit for review

### 3. Store Listing Requirements
- **App Icon**: 512x512 PNG
- **Feature Graphic**: 1024x500 PNG
- **Screenshots**: At least 2 phone screenshots
- **Privacy Policy**: Required for apps with sensitive permissions
- **App Description**: Clear, compelling description

## Post-Deployment Monitoring

### 1. Crash Reporting
- Monitor crash reports in Play Console
- Set up Firebase Crashlytics (optional)

### 2. Performance Monitoring
- Monitor ANR (Application Not Responding) rates
- Track app startup time
- Monitor memory usage

### 3. User Feedback
- Monitor user reviews and ratings
- Respond to user feedback promptly
- Track feature usage analytics

## Rollback Plan

### 1. If Critical Issues Found
1. Stop rollout in Play Console
2. Fix issues in new version
3. Upload hotfix build
4. Resume rollout with fixed version

### 2. Version Management
- Keep previous working APK/AAB files
- Maintain version history
- Document all changes in CHANGELOG.md

## Environment-Specific Configurations

### Development
```properties
# local.properties for development
HUGGINGFACE_API_KEY=hf_dev_token
GEMINI_API_KEY=dev_gemini_key
DOG_API_KEY=dev_dog_api_key
```

### Production
```properties
# local.properties for production
HUGGINGFACE_API_KEY=hf_production_token
GEMINI_API_KEY=production_gemini_key
DOG_API_KEY=production_dog_api_key
```

## Continuous Integration (Optional)

### GitHub Actions Example
```yaml
name: Build and Deploy
on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    - name: Build Release AAB
      run: ./gradlew bundleRelease
    - name: Upload to Play Store
      # Use play-store-upload action
```

## Support and Maintenance

### 1. Regular Updates
- Monitor API changes from third-party services
- Update dependencies regularly
- Address security vulnerabilities promptly

### 2. User Support
- Set up support email
- Create FAQ documentation
- Monitor app store reviews

### 3. Analytics (Optional)
- Implement Firebase Analytics
- Track user engagement metrics
- Monitor feature usage

---

**Important**: Never commit sensitive information like API keys, keystores, or passwords to version control. Always use environment variables or secure configuration management for production deployments.