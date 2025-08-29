"""
Setup script to install the correct TensorFlow version for compatible model conversion
Run this before converting your model to ensure compatibility with Android TensorFlow Lite 2.14.0
"""

import subprocess
import sys

def install_compatible_tensorflow():
    """Install TensorFlow 2.13.0 for maximum compatibility"""
    
    print("🔧 Setting up compatible TensorFlow environment...")
    print("📦 This will install TensorFlow 2.13.0 (compatible with Android TFLite 2.14.0)")
    
    # Uninstall existing TensorFlow
    print("\n1️⃣ Uninstalling existing TensorFlow...")
    try:
        subprocess.run([sys.executable, "-m", "pip", "uninstall", "tensorflow", "-y"], 
                      check=True, capture_output=True, text=True)
        print("✅ Existing TensorFlow uninstalled")
    except subprocess.CalledProcessError:
        print("ℹ️  No existing TensorFlow found")
    
    # Install compatible version
    print("\n2️⃣ Installing TensorFlow 2.13.0...")
    try:
        subprocess.run([sys.executable, "-m", "pip", "install", "tensorflow==2.13.0"], 
                      check=True, capture_output=True, text=True)
        print("✅ TensorFlow 2.13.0 installed successfully")
    except subprocess.CalledProcessError as e:
        print(f"❌ Failed to install TensorFlow 2.13.0: {e}")
        return False
    
    # Verify installation
    print("\n3️⃣ Verifying installation...")
    try:
        import tensorflow as tf
        print(f"✅ TensorFlow {tf.__version__} is ready")
        
        if tf.__version__.startswith("2.13"):
            print("🎉 Perfect! This version should generate compatible .tflite models")
            return True
        else:
            print(f"⚠️  Warning: Expected 2.13.x but got {tf.__version__}")
            return False
            
    except ImportError as e:
        print(f"❌ Failed to import TensorFlow: {e}")
        return False

def main():
    print("=" * 60)
    print("TensorFlow Compatibility Setup")
    print("=" * 60)
    
    success = install_compatible_tensorflow()
    
    print("\n" + "=" * 60)
    if success:
        print("🎉 Setup complete! You can now run convert_model.py")
        print("📝 Next steps:")
        print("   1. Update file paths in convert_model.py")
        print("   2. Run: python convert_model.py")
        print("   3. Replace your .tflite file with the generated compatible version")
    else:
        print("❌ Setup failed. Please install TensorFlow 2.13.0 manually:")
        print("   pip install tensorflow==2.13.0")
    print("=" * 60)

if __name__ == "__main__":
    main()
