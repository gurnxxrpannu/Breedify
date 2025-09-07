# 🐾 Breedify

**Your AI-powered dog breed identification and exploration companion**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![API Level](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Breedify is a production-ready Android application built with Jetpack Compose that combines advanced machine learning with comprehensive breed data to help users identify, explore, and learn about dog breeds. Featuring multiple AI-powered identification methods including Hugging Face models, TensorFlow Lite integration, and Google's Gemini AI, along with a beautiful, intuitive interface powered by The Dog API.

## ✨ Key Features

### 🤖 AI-Powered Breed Identification
- **Multiple ML Models**: Hugging Face API integration with fallback models
  - Primary: Microsoft ResNet-50
  - Fallback: SkyAU Dog Breed Classifier ViT
  - Tertiary: Google ViT Base Patch16-224
- **TensorFlow Lite**: Local on-device model for offline identification
- **Gemini AI Integration**: Google's advanced AI for breed analysis and chatbot
- **Camera Integration**: Real-time photo capture with CameraX
- **Gallery Upload**: Import photos from device gallery
- **Confidence Scoring**: Accuracy percentages for predictions

### 🏠 Home Screen
- **Recommended Breeds**: Curated list from The Dog API
- **Quick Actions**: Easy access to camera and upload features
- **Daily Facts**: Random dog breed facts
- **Modern Design**: Clean, pet-friendly interface with soft green theme
- **Integrated Navigation**: Custom bottom navigation with floating chatbot button

### 🔍 Explore Screen
- **The Dog API Integration**: Real-time breed data from comprehensive database
- **Smart Search**: Find breeds by name with debounced search
- **Comprehensive Database**: Access to 200+ dog breeds with detailed information
- **Pagination**: Load more functionality with smooth animations
- **Rich Breed Data**: Temperament, size, origin, life span, and characteristics
- **High-Quality Images**: Professional breed photos from The Dog API

### 💬 AI Chatbot
- **Gemini AI Powered**: Advanced conversational AI for breed questions
- **Image Analysis**: Upload photos for detailed breed analysis
- **Breed Information**: Get comprehensive breed details through chat
- **Natural Language**: Ask questions in plain English

### 🎯 Breed Details
- **Comprehensive Information**: Detailed breed characteristics
- **Visual Gallery**: Multiple high-quality breed images
- **Breed Facts**: Temperament, size, origin, and care requirements
- **API Integration**: Real-time data from The Dog API

### ❤️ Favorites System
- **Save Breeds**: Mark favorite breeds for quick access
- **Persistent Storage**: Favorites saved locally
- **Easy Management**: Add/remove favorites with simple tap

## 🛠️ Technical Architecture

### **Frontend & UI**
- **Jetpack Compose**: Modern declarative UI toolkit with Material Design 3
- **Kotlin**: 100% Kotlin codebase with coroutines and modern language features
- **Custom Components**: Reusable UI components with smooth animations
- **Responsive Design**: Optimized for different screen sizes and orientations
- **Dark/Light Theme**: Adaptive theming with system preferences

### **Architecture Pattern**
- **MVVM**: Model-View-ViewModel architecture with clear separation of concerns
- **Repository Pattern**: Clean data layer abstraction for API and local data
- **Dependency Injection**: Structured service locators with proper lifecycle management
- **State Management**: Reactive state handling with Compose and coroutines
- **Error Handling**: Centralized error management with user-friendly messaging

### **AI & Machine Learning**
- **Hugging Face API**: Cloud-based  breed identification with multiple model fallbacks
  - Microsoft ResNet-50 (Primary model)
  - SkyAU Dog Breed Classifier ViT (Fallback)
  - Google ViT Base Patch16-224 (Tertiary)
- **TensorFlow Lite**: On-device model for offline processing and privacy
- **Gemini AI**: Google's generative AI for conversational chatbot and image analysis
- **Image Processing**: Optimized preprocessing pipeline with memory management

### **Networking & APIs**
- **The Dog API**: Comprehensive breed database with 200+ breeds
- **Retrofit**: Type-safe HTTP client with coroutines support
- **OkHttp**: Network layer with interceptors for logging and authentication
- **Gson**: JSON serialization with custom adapters
- **Coil**: Efficient image loading with memory and disk caching
- **Network Monitoring**: Connection state awareness and offline handling

### **Camera & Media**
- **CameraX**: Modern camera API for photo capture with lifecycle awareness
- **Accompanist Permissions**: Runtime permission handling with rationale
- **File Management**: Secure file storage in app-specific directories
- **Image Processing**: Bitmap manipulation with memory optimization
- **Gallery Integration**: Photo picker with proper URI handling

### **Security & Production**
- **ProGuard/R8**: Code obfuscation and optimization for release builds
- **BuildConfig**: Secure API key management through build system
- **Certificate Pinning**: Network security for production environments
- **Root Detection**: Security measures for sensitive operations
- **Logging**: Production-safe logging with debug removal

## 📱 App Structure

```
app/src/main/java/com/example/breedify/
├── components/                    # Reusable UI components
│   └── DogBreedCard.kt           # Animated breed cards
├── data/                         # Data layer
│   ├── api/                      # API services and models
│   │   ├── DogApiService.kt      # The Dog API interface
│   │   ├── GeminiApiService.kt   # Gemini AI service
│   │   └── HuggingFaceApiService.kt # ML model API
│   └── repository/               # Data repositories
│       ├── DogRepository.kt      # Breed data management
│       └── GeminiRepository.kt   # AI service management
├── navigation/                   # Navigation components
│   └── BottomNavigation.kt       # Custom bottom nav with cutout
├── screens/                      # Screen composables
│   ├── homeScreen/              # Home with recommendations
│   ├── exploreScreen/           # Search and browse breeds
│   ├── cameraScreen/            # Camera for identification
│   ├── chatbotScreen/           # AI-powered chatbot
│   ├── dogDetailScreen/         # Detailed breed information
│   ├── favoritesScreen/         # Saved favorite breeds
│   ├── prediction/              # ML prediction results
│   └── welcomeScreen/           # App introduction
├── ui/theme/                    # Design system
│   ├── Color.kt                 # Color palette
│   ├── Theme.kt                 # Material theme
│   └── Type.kt                  # Typography
├── utils/                       # Utility classes
│   ├── AppConfig.kt             # App configuration and feature flags
│   ├── CameraUtils.kt           # Camera and image utilities
│   ├── Constants.kt             # App constants and configuration
│   ├── ErrorHandler.kt          # Centralized error handling and user messaging
│   ├── Logger.kt                # Production-safe logging utilities
│   ├── MLUtils.kt               # ML model utilities and image processing
│   ├── NetworkUtils.kt          # Network connectivity and monitoring
│   └── Result.kt                # Result wrapper for safe API calls
└── MainActivity.kt              # Main activity with navigation
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog | 2023.1.1 or newer (Iguana recommended)
- **Android SDK**: API 24 (Android 7.0) minimum, API 34+ recommended
- **Kotlin**: 1.9.0 or newer
- **Java**: JDK 11 or higher
- **Gradle**: 8.0+ (handled by wrapper)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/gurnxxrpannu/Breedify.git
   cd Breedify
   ```

2. **Set up API keys**
   Create `local.properties` file in the root directory:
   ```properties
   # SDK location (auto-generated by Android Studio)
   sdk.dir=/path/to/your/android/sdk
   
   # Required API Keys - Replace with your actual keys
   HUGGINGFACE_API_KEY=hf_your_huggingface_token_here
   GEMINI_API_KEY=
   DOG_API_KEY=your_dog_api_key_here
   ```

3. **Get API Keys** (Free tiers available)
   - **Hugging Face**: [Sign up](https://huggingface.co) → Settings → Access Tokens
   - **Gemini AI**: [Google AI Studio](https://makersuite.google.com/app/apikey) → Create API Key
   - **The Dog API**: [Register](https://thedogapi.com) → Get free API key

4. **Build and Run**
   ```bash
   # Open in Android Studio or use command line
   ./gradlew assembleDebug
   
   # Install on connected device
   ./gradlew installDebug
   ```

### Development Setup

#### Android Studio
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the cloned Breedify directory
4. Wait for Gradle sync to complete
5. Connect device or start emulator
6. Run the app (Shift+F10)

#### Command Line
```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build (requires signing setup)
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Required Permissions
The app requests these permissions at runtime:
- **Camera** (`CAMERA`): For capturing dog photos
- **Storage** (`READ_EXTERNAL_STORAGE`): For accessing gallery images
- **Internet** (`INTERNET`): For API calls and image loading
- **Network State** (`ACCESS_NETWORK_STATE`): For connectivity monitoring

## 🎨 Design System

### Color Palette
- **Background**: Soft mint green (`#D4E6D4`)
- **Primary**: Blue accent (`#4A90E2`)
- **Secondary**: Green accent (`#5CB85C`)
- **Cards**: Pure white (`#FFFFFF`)
- **Text Primary**: Dark gray (`#2D3748`)
- **Text Secondary**: Medium gray (`#6B7280`)

### Key Components

#### Custom Bottom Navigation
- Unique cutout design with floating paw button
- Smooth state transitions between screens
- Custom shape implementation with rounded corners
- Integrated chatbot access

#### DogBreedCard
- Animated breed cards with tap effects
- Skeleton loading states during data fetch
- High-quality image loading with Coil
- Responsive sizing for different screen densities

#### Search & Filtering
- Real-time search with debounced input
- Loading states management
- Pagination support for large datasets

## 🔧 API Integration Details

### The Dog API
- **Base URL**: `https://api.thedogapi.com/`
- **Endpoints Used**:
  - `/v1/breeds` - Get all breeds
  - `/v1/breeds/search` - Search breeds by name
  - `/v1/breeds/{id}` - Get breed details
  - `/v1/images/{id}` - Get breed images
- **Features**: Pagination, search, detailed breed information

### Hugging Face API
- **Models Used**:
  - `microsoft/resnet-50` (Primary)
  - `skyau/dog-breed-classifier-vit` (Fallback)
  - `google/vit-base-patch16-224` (Tertiary)
- **Input**: Binary image data (JPEG/PNG)
- **Output**: Breed predictions with confidence scores

### Gemini AI
- **Model**: `gemini-1.5-flash`
- **Capabilities**: 
  - Image analysis and breed identification
  - Natural language conversations about breeds
  - Comprehensive breed information generation

## 🧪 Testing & Quality Assurance

### Automated Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Manual Testing Checklist
- [ ] **Camera Functionality**: Photo capture and processing
- [ ] **Gallery Upload**: Image selection and ML processing
- [ ] **Search & Browse**: Breed search with various queries
- [ ] **Navigation**: All screen transitions and back navigation
- [ ] **API Integration**: All API endpoints and error scenarios
- [ ] **Network Handling**: Offline mode and connectivity changes
- [ ] **Permissions**: Runtime permission flows
- [ ] **Performance**: Memory usage and app responsiveness
- [ ] **UI/UX**: Different screen sizes and orientations

### Production Testing
- [ ] **Release Build**: Test with ProGuard enabled
- [ ] **API Keys**: Verify production API key configuration
- [ ] **Performance**: Memory leaks and battery usage
- [ ] **Security**: Network traffic and data protection
- [ ] **Compatibility**: Multiple Android versions (API 24-34)

## 🔒 Security & Privacy

### Security Features
- **API Key Protection**: All keys stored in `local.properties` and BuildConfig
- **Code Obfuscation**: ProGuard/R8 enabled for release builds
- **Certificate Pinning**: Network security for production (configurable)
- **Root Detection**: Security measures for sensitive operations
- **No Hardcoded Secrets**: All sensitive data externalized
- **Secure Storage**: App-specific directories for file storage

### Privacy Measures
- **Local Processing**: TensorFlow Lite for on-device ML inference
- **Minimal Data Collection**: Only necessary data for functionality
- **No User Tracking**: No analytics or tracking without consent
- **Secure Network**: HTTPS-only API communications
- **Data Retention**: Images processed locally, not stored on servers

### Production Security
```kotlin
// ProGuard rules for security
-assumenosideeffects class android.util.Log {
    public static *** d(...);  // Removes debug logs
}

// API model protection
-keep class com.example.breedify.data.api.** { *; }
```

## 🚧 Roadmap & Future Features

### Version 1.1 (Planned)
- [ ] **Offline Mode**: Local breed database with Room
- [ ] **Enhanced Search**: Advanced filtering by size, temperament, energy
- [ ] **Breed Comparison**: Side-by-side breed analysis
- [ ] **User Profiles**: Personalized breed recommendations
- [ ] **Favorites Sync**: Cloud backup of favorite breeds

### Version 1.2 (Future)
- [ ] **Social Features**: Share breeds and photos with community
- [ ] **Push Notifications**: Daily breed facts and care tips
- [ ] **AR Features**: Augmented reality breed identification
- [ ] **Veterinary Integration**: Connect with local vets and services
- [ ] **Breeding Information**: Responsible breeding resources

### Technical Roadmap
- [ ] **Room Database**: Local data persistence and caching
- [ ] **Hilt/Dagger**: Proper dependency injection framework
- [ ] **Compose Navigation**: Modern navigation component
- [ ] **Paging 3**: Efficient data loading and pagination
- [ ] **WorkManager**: Background sync and notifications
- [ ] **Unit Tests**: Comprehensive test coverage (target: 80%+)
- [ ] **UI Tests**: Automated UI testing with Espresso
- [ ] **Performance**: Memory optimization and startup time improvements

### Internationalization
- [ ] **Multi-language Support**: Spanish, French, German, Japanese
- [ ] **Regional Breed Data**: Location-specific breed information
- [ ] **Cultural Adaptation**: Region-appropriate UI and content

## 🤝 Contributing

We welcome contributions from the community! Here's how to get involved:

### Development Setup
1. **Fork** the repository on GitHub
2. **Clone** your fork locally
   ```bash
   git clone https://github.com/your-username/Breedify.git
   cd Breedify
   ```
3. **Create** a feature branch
   ```bash
   git checkout -b feature/your-amazing-feature
   ```
4. **Set up** your development environment
   - Configure `local.properties` with API keys
   - Sync project in Android Studio
   - Run tests to ensure everything works

### Code Standards
- **Kotlin Style**: Follow [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Architecture**: Maintain MVVM pattern and repository structure
- **Documentation**: Add KDoc comments for public APIs
- **Error Handling**: Use centralized `ErrorHandler` for user-facing errors
- **Testing**: Write tests for new functionality
- **Performance**: Consider memory usage and network efficiency

### Contribution Types
- 🐛 **Bug Fixes**: Fix issues and improve stability
- ✨ **Features**: Add new functionality (discuss in issues first)
- 📚 **Documentation**: Improve README, code comments, or guides
- 🎨 **UI/UX**: Enhance user interface and experience
- ⚡ **Performance**: Optimize code and reduce resource usage
- 🧪 **Testing**: Add or improve test coverage

### Pull Request Process
1. **Update** documentation for any new features
2. **Add tests** for new functionality
3. **Run** all tests and ensure they pass
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```
4. **Update** CHANGELOG.md with your changes
5. **Create** a detailed pull request description
6. **Request** review from maintainers

### Issue Reporting
When reporting bugs, please include:
- Android version and device model
- App version and build type (debug/release)
- Steps to reproduce the issue
- Expected vs actual behavior
- Screenshots or logs if applicable

### Feature Requests
For new features, please:
- Check existing issues to avoid duplicates
- Describe the use case and benefits
- Consider implementation complexity
- Discuss with maintainers before starting work

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **The Dog API**: Comprehensive dog breed database and images
- **Hugging Face**: Pre-trained machine learning models for breed identification
- **Google Gemini AI**: Advanced AI capabilities for chatbot and image analysis
- **TensorFlow**: Machine learning framework for on-device processing
- **Jetpack Compose**: Modern Android UI toolkit
- **Material Design**: Google's design system for beautiful interfaces
- **Open Source Community**: Amazing libraries and tools that made this possible

## 📞 Support

If you encounter any issues or have questions:

1. **Check the Issues**: Look for existing solutions in GitHub Issues
2. **Create an Issue**: Report bugs or request features
3. **Documentation**: Refer to this README and inline code comments
4. **API Documentation**: Check respective API documentation for service-specific issues

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| **Language** | 100% Kotlin |
| **UI Framework** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM with Repository Pattern |
| **API Integrations** | 3 (The Dog API, Hugging Face, Gemini AI) |
| **ML Models** | Multiple Hugging Face models + TensorFlow Lite |
| **Minimum SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 34 (Android 14) |
| **Build System** | Gradle with Kotlin DSL |
| **Code Quality** | ProGuard/R8 optimized, 0 deprecation warnings |

## 📱 Download & Support

### Installation
- **Google Play Store**: Coming soon
- **GitHub Releases**: [Download APK](https://github.com/gurnxxrpannu/Breedify/releases)
- **Build from Source**: Follow the [Getting Started](#-getting-started) guide

### Support & Community
- **Issues**: [GitHub Issues](https://github.com/gurnxxrpannu/Breedify/issues)
- **Discussions**: [GitHub Discussions](https://github.com/gurnxxrpannu/Breedify/discussions)
- **Email**: gurnxxrpannu@gmail.com
- **Documentation**: [Wiki](https://github.com/gurnxxrpannu/Breedify/wiki)

### Project Status
- ✅ **Production Ready**: Fully optimized for release
- 🔄 **Active Development**: Regular updates and improvements
- 🛡️ **Security Audited**: ProGuard enabled, secure API handling
- 📱 **Cross-Device**: Tested on phones and tablets
- 🌍 **Open Source**: MIT License, community contributions welcome

---

<div align="center">

**Made with ❤️ and 🐾 for dog lovers everywhere**

*Breedify - Discover, identify, and learn about your perfect canine companion*

[![GitHub stars](https://img.shields.io/github/stars/gurnxxrpannu/Breedify?style=social)](https://github.com/gurnxxrpannu/Breedify/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/gurnxxrpannu/Breedify?style=social)](https://github.com/gurnxxrpannu/Breedify/network/members)
[![GitHub issues](https://img.shields.io/github/issues/gurnxxrpannu/Breedify)](https://github.com/gurnxxrpannu/Breedify/issues)

</div>
