package com.example.breedify.screens.chatbotScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breedify.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val geminiRepository = GeminiRepository()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun sendMessage(text: String) {
        // Add user message
        val userMessage = ChatMessage(text = text, isFromUser = true)
        _messages.value = _messages.value + userMessage
        
        // Set loading state
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val response = geminiRepository.generateResponse(text)
                val botMessage = ChatMessage(text = response, isFromUser = false)
                _messages.value = _messages.value + botMessage
            } catch (e: Exception) {
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
}