import tensorflow as tf
import numpy as np

def check_tensorflow_version():
    """Check and recommend TensorFlow version for compatibility"""
    print(f"Current TensorFlow version: {tf.__version__}")
    
    # Recommend compatible version
    compatible_version = "2.13.0"  # Known to work with Android TFLite 2.14.0
    
    if tf.__version__.startswith("2.15") or tf.__version__.startswith("2.16"):
        print(f"⚠️  WARNING: TensorFlow {tf.__version__} may generate FULLY_CONNECTED v12")
        print(f"📋 RECOMMENDED: Downgrade to TensorFlow {compatible_version}")
        print(f"💻 Run: pip install tensorflow=={compatible_version}")
        print("🔄 Then re-run this script")
        return False
    elif tf.__version__.startswith("2.13") or tf.__version__.startswith("2.14"):
        print(f"✅ TensorFlow {tf.__version__} should generate compatible operations")
        return True
    else:
        print(f"❓ TensorFlow {tf.__version__} compatibility unknown")
        print(f"📋 RECOMMENDED: Use TensorFlow {compatible_version}")
        return True

def convert_model_for_compatibility():
    """
    Convert your existing model to be compatible with TensorFlow Lite 2.14.0
    This script forces compatibility with older operation versions
    """
    
    # Check TensorFlow version first
    if not check_tensorflow_version():
        return
    
    # Update these paths to your actual model files
    saved_model_path = "path/to/your/saved_model"  # Update this path
    h5_model_path = "path/to/your/model.h5"        # Update this path
    
    # Try different loading methods
    converter = None
    
    # Option 1: If you have a SavedModel
    try:
        print("Trying to load SavedModel...")
        converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_path)
        print("✅ SavedModel loaded successfully")
    except Exception as e:
        print(f"❌ SavedModel loading failed: {e}")
        
        # Option 2: If you have an H5 model
        try:
            print("Trying to load H5 model...")
            model = tf.keras.models.load_model(h5_model_path)
            converter = tf.lite.TFLiteConverter.from_keras_model(model)
            print("✅ H5 model loaded successfully")
        except Exception as e:
            print(f"❌ H5 model loading failed: {e}")
            print("Please update the paths in the script to your actual model files")
            return
    
    if converter is None:
        print("❌ Could not load any model. Please check your file paths.")
        return
    
    # Compatibility settings for older TensorFlow Lite versions
    print("Setting compatibility options...")
    
    # Use only basic TensorFlow Lite operations (forces older versions)
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
    
    # Disable newer optimizations that might use v12 operations
    converter.optimizations = []  # No optimizations to avoid newer ops
    
    # Keep float32 to avoid quantization complications
    converter.inference_input_type = tf.float32
    converter.inference_output_type = tf.float32
    
    # Explicitly disable experimental features
    converter.experimental_new_converter = False
    converter.allow_custom_ops = False
    
    # Convert the model
    try:
        print("Converting model...")
        tflite_model = converter.convert()
        
        # Save the compatible model
        output_file = 'dog_breed_model_compatible.tflite'
        with open(output_file, 'wb') as f:
            f.write(tflite_model)
            
        print(f"✅ Model converted successfully!")
        print(f"📁 Saved as: {output_file}")
        print(f"📊 Model size: {len(tflite_model)} bytes")
        print("🔄 Replace your existing 'dog_breed_model.tflite' with this file")
        
    except Exception as e:
        print(f"❌ Conversion failed: {e}")
        print("Trying alternative conversion method...")
        alternative_conversion(converter)

def alternative_conversion():
    """
    Alternative conversion method with more aggressive compatibility settings
    """
    # If the above doesn't work, try this more compatible approach
    converter.representative_dataset = representative_data_gen  # You'll need to implement this
    converter.target_spec.supported_types = [tf.float16]
    converter.inference_input_type = tf.float32
    converter.inference_output_type = tf.float32
    
    # More aggressive compatibility
    converter.allow_custom_ops = True
    converter.experimental_new_converter = False

if __name__ == "__main__":
    convert_model_for_compatibility()
