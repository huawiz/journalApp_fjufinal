// viewmodel/DiaryViewModel.kt
package com.example.goodtimestamp.viewmodel

import androidx.lifecycle.*
import com.example.goodtimestamp.data.entity.DiaryEntry
import com.example.goodtimestamp.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {
    val allEntries: StateFlow<List<DiaryEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentEntry = MutableStateFlow<DiaryEntry?>(null)
    val currentEntry: StateFlow<DiaryEntry?> = _currentEntry.asStateFlow()

    fun insertEntry(title: String, content: String, intensity: Int, energy: Int) = viewModelScope.launch {
        val entry = DiaryEntry(
            title = title,
            content = content,
            intensity = intensity,
            energy = energy
        )
        repository.insert(entry)
    }

    fun updateEntry(entry: DiaryEntry) = viewModelScope.launch {
        repository.update(entry)
    }

    fun deleteEntry(entry: DiaryEntry) = viewModelScope.launch {
        repository.delete(entry)
    }

    fun loadEntry(id: Long) = viewModelScope.launch {
        _currentEntry.value = repository.getEntry(id)
    }

}

