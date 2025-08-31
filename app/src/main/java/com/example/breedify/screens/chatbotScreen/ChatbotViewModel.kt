package com.example.breedify.screens.chatbotScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breedify.data.repository.GeminiRepository
import com.example.breedify.utils.Constants
import com.example.breedify.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing chatbot conversation state and interactions
 */
class ChatbotViewModel : ViewModel() {
    private val geminiRepository = GeminiRepository()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    companion object {
        private const val TAG = "ChatbotViewModel"
    }
    
    /**
     * Sends a message to the chatbot and handles the response
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) {
            Logger.w("Attempted to send blank message", TAG)
            return
        }
        
        if (text.length > Constants.MAX_MESSAGE_LENGTH) {
            Logger.w("Message too long: ${text.length} characters", TAG)
            val errorMessage = ChatMessage(
                text = "Message is too long. Please keep it under ${Constants.MAX_MESSAGE_LENGTH} characters.",
                isFromUser = false
            )
            _messages.value = _messages.value + errorMessage
            return
        }
        
        // Limit chat history
        if (_messages.value.size >= Constants.CHAT_HISTORY_LIMIT) {
            _messages.value = _messages.value.drop(2) // Remove oldest user-bot pair
        }
        
        // Add user message
        val userMessage = ChatMessage(text = text.trim(), isFromUser = true)
        _messages.value = _messages.value + userMessage
        
        Logger.d("User message added: ${text.take(50)}...", TAG)
        
        // Set loading state
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val response = geminiRepository.generateResponse(text.trim())
                val botMessage = ChatMessage(text = response, isFromUser = false)
                _messages.value = _messages.value + botMessage
                
                Logger.d("Bot response added successfully", TAG)
            } catch (e: Exception) {
                Logger.e("Error getting bot response", e, TAG)
                
                val errorMessage = ChatMessage(
                    text = "Sorry, I'm having trouble responding right now. Please try again later.",
                    isFromUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clears the chat history
     */
    fun clearChat() {
        Logger.d("Clearing chat history", TAG)
        _messages.value = emptyList()
    }
}