# API Keys Setup Guide

This app requires API keys for various services to function properly. Follow the instructions below
to set up your API keys.

## 1. Gemini API Key (Required for AI breed identification)

### Step 1: Get your Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated API key

### Step 2: Add the API key to your project

1. Open the `local.properties` file in your project root
2. Replace `your_actual_gemini_api_key_here` with your actual API key:
   ```
   GEMINI_API_KEY=AIzaSyC1234567890abcdefghijklmnopqrstuvwxyz
   ```

## 2. Dog API Key (Optional - for additional breed data)

### Step 1: Get your Dog API Key

1. Go to [The Dog API](https://thedogapi.com/)
2. Sign up for a free account
3. Get your API key from the dashboard

### Step 2: Add to local.properties

```
DOG_API_KEY=your_dog_api_key_here
```

## 3. Hugging Face API Key (Optional - for ML model backup)

### Step 1: Get your Hugging Face API Key

1. Go to [Hugging Face](https://huggingface.co/)
2. Sign up for an account
3. Go to Settings > Access Tokens
4. Create a new token

### Step 2: Add to local.properties

```
HUGGINGFACE_API_KEY=hf_your_token_here
```

## Important Notes

- **Never commit the `local.properties` file to version control** - it contains sensitive API keys
- The `local.properties` file is already added to `.gitignore`
- The Gemini API key is required for the AI breed identification feature to work
- Other API keys are optional but provide additional functionality

## Troubleshooting

If you get an error like "Method doesn't allow unregistered callers", it means:

1. Your API key is missing or incorrect
2. Your API key doesn't have the required permissions
3. You need to enable the Generative AI API in Google Cloud Console

Make sure your Gemini API key is properly set up and has access to the Generative AI API.