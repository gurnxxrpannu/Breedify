# 🐾 Breedify

**Your AI-powered dog breed identification and exploration companion**

Breedify is a modern Android application built with Jetpack Compose that combines advanced machine learning with comprehensive breed data to help users identify, explore, and learn about dog breeds. Featuring multiple AI-powered identification methods including Hugging Face models, TensorFlow Lite integration, and Google's Gemini AI, along with a beautiful, intuitive interface powered by The Dog API.

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
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material Design 3**: Latest design system with custom theming
- **Kotlin**: 100% Kotlin codebase with coroutines
- **Custom Components**: Reusable UI components with animations
- **Responsive Design**: Optimized for different screen sizes

### **Architecture Pattern**
- **MVVM**: Model-View-ViewModel architecture
- **Repository Pattern**: Clean data layer abstraction
- **Dependency Injection**: Manual DI with service locators
- **State Management**: Compose state handling with remember/mutableState

### **AI & Machine Learning**
- **Hugging Face API**: Cloud-based breed identification
  - Microsoft ResNet-50 (Primary)
  - SkyAU Dog Breed Classifier ViT (Fallback)
  - Google ViT Base Patch16-224 (Tertiary)
- **TensorFlow Lite**: On-device model for offline processing
- **Gemini AI**: Google's generative AI for chatbot and image analysis
- **Image Processing**: Optimized preprocessing pipeline

### **Networking & APIs**
- **The Dog API**: Comprehensive breed database
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: Network layer with logging interceptors
- **Gson**: JSON serialization/deserialization
- **Coil**: Efficient image loading and caching

### **Camera & Media**
- **CameraX**: Modern camera API for photo capture
- **Accompanist Permissions**: Runtime permission handling
- **File Management**: Secure file storage in app directory
- **Image Processing**: Bitmap manipulation and optimization

### **Security**
- **BuildConfig**: Secure API key management
- **Local Properties**: Environment-specific configuration
- **No Hardcoded Keys**: All sensitive data externalized

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
│   ├── CameraUtils.kt           # Camera and image utilities
│   ├── Constants.kt             # App constants
│   ├── MLUtils.kt               # ML model utilities
│   └── HuggingFaceApiTest.kt    # API testing utilities
└── MainActivity.kt              # Main activity with navigation
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog | 2023.1.1 or newer
- **Android SDK**: API 24 (Android 7.0) or higher
- **Kotlin**: 1.9.0 or newer
- **Java**: JDK 11 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/gurnxxrpannu/Breedify.git
   cd Breedify
   ```

2. **Set up API keys**
   Create or update `local.properties` file in the root directory:
   ```properties
   # SDK location
   sdk.dir=/path/to/your/android/sdk
   
   # API Keys - Replace with your actual keys
   HUGGINGFACE_API_KEY=hf_your_huggingface_token_here
   GEMINI_API_KEY=your_gemini_api_key_here
   DOG_API_KEY=your_dog_api_key_here
   ```

3. **Get API Keys**
   - **Hugging Face**: Sign up at [huggingface.co](https://huggingface.co) and get your API token
   - **Gemini AI**: Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
   - **The Dog API**: Register at [thedogapi.com](https://thedogapi.com) for your API key

4. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

5. **Build and Run**
   - Sync project with Gradle files
   - Connect an Android device or start an emulator
   - Click "Run" or press `Ctrl+R`

### Required Permissions
The app requires the following permissions:
- **Camera**: For taking photos of dogs
- **Storage**: For accessing gallery images
- **Internet**: For API calls and image loading

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

## 🧪 Testing

### API Testing
The app includes built-in API testing functionality:
- **HuggingFaceApiTest**: Test ML model connectivity
- **ApiTestScreen**: Interactive testing interface
- **Error Handling**: Comprehensive error reporting

### Manual Testing
1. **Camera Functionality**: Test photo capture and processing
2. **Gallery Upload**: Test image selection and upload
3. **Search**: Test breed search with various queries
4. **Navigation**: Test all screen transitions
5. **API Calls**: Verify all API integrations work correctly

## 🔒 Security Features

- **API Key Protection**: All keys stored in `local.properties`
- **BuildConfig Integration**: Secure key access through build system
- **No Hardcoded Secrets**: All sensitive data externalized
- **Git Ignore**: `local.properties` excluded from version control

## 🚧 Future Roadmap

### Planned Features
- [ ] **Offline Mode**: Cache breed data for offline access
- [ ] **Push Notifications**: Daily breed facts and tips
- [ ] **Social Features**: Share breeds and photos with friends
- [ ] **Advanced Filters**: Filter by size, temperament, energy level
- [ ] **Breed Comparison**: Side-by-side breed comparisons
- [ ] **Dark Mode**: Complete dark theme implementation
- [ ] **Localization**: Multi-language support
- [ ] **User Profiles**: Personalized recommendations

### Technical Improvements
- [ ] **Room Database**: Local data persistence
- [ ] **Hilt/Dagger**: Proper dependency injection
- [ ] **Compose Navigation**: Replace manual navigation
- [ ] **Paging 3**: Improved pagination handling
- [ ] **WorkManager**: Background data sync
- [ ] **Unit Tests**: Comprehensive test coverage

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Development Setup
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Set up your `local.properties` with API keys
4. Make your changes following the existing code style
5. Test thoroughly on different devices
6. Commit with meaningful messages
7. Push to your branch and create a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure proper error handling
- Test on multiple screen sizes

### Pull Request Process
1. Update documentation if needed
2. Add tests for new functionality
3. Ensure all existing tests pass
4. Update the README if you add new features
5. Request review from maintainers

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

## 📊 Project Stats

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **API Integrations**: 3 (The Dog API, Hugging Face, Gemini AI)
- **ML Models**: Multiple Hugging Face models + TensorFlow Lite
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 14)

---

**Made with ❤️ and 🐾 for dog lovers everywhere**

*Breedify - Discover, identify, and learn about your perfect canine companion*

**Download**: Coming soon to Google Play Store
**Website**: [Coming Soon]
**Contact**: [Your Contact Information]