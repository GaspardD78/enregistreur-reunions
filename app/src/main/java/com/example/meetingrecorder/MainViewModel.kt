package com.example.meetingrecorder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val recordingDao = AppDatabase.getDatabase(application).recordingDao()
    private val audioRecorder = AudioRecorder(application)
    private val apiKeyManager = ApiKeyManager(application)

    val recordings = recordingDao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _transcriptionState = MutableStateFlow<TranscriptionState>(TranscriptionState.Idle)
    val transcriptionState = _transcriptionState.asStateFlow()

    private var currentRecordingFile: File? = null

    fun startRecording() {
        currentRecordingFile = File(getApplication<Application>().cacheDir, "recording_${System.currentTimeMillis()}.mp4")
        audioRecorder.start(currentRecordingFile!!)
    }

    fun stopRecording(duration: Long) {
        audioRecorder.stop()
        val recording = Recording(
            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            duration = formatDuration(duration),
            filePath = currentRecordingFile?.absolutePath ?: ""
        )
        viewModelScope.launch {
            recordingDao.insert(recording)
        }
    }

    fun transcribeRecording(recording: Recording) {
        viewModelScope.launch {
            _transcriptionState.value = TranscriptionState.Loading("Uploading file...")
            val assemblyaiApiKey = apiKeyManager.getAssemblyAiApiKey()
            val mistralApiKey = apiKeyManager.getMistralApiKey()

            if (assemblyaiApiKey == null || mistralApiKey == null) {
                _transcriptionState.value = TranscriptionState.Error("API keys not set")
                return@launch
            }

            val apiService = ApiService(assemblyaiApiKey, mistralApiKey)
            val audioUrl = apiService.uploadFile(File(recording.filePath))

            _transcriptionState.value = TranscriptionState.Loading("Creating transcript...")
            val transcriptId = apiService.createTranscript(audioUrl)

            _transcriptionState.value = TranscriptionState.Loading("Waiting for transcript...")
            val transcript = apiService.getTranscript(transcriptId)

            if (transcript.status == "completed") {
                _transcriptionState.value = TranscriptionState.Loading("Summarizing...")
                val summary = apiService.getSummary(transcript.text ?: "")
                _transcriptionState.value = TranscriptionState.Success(summary)
            } else {
                _transcriptionState.value = TranscriptionState.Error(transcript.text ?: "Unknown error")
            }
        }
    }

    fun resetTranscriptionState() {
        _transcriptionState.value = TranscriptionState.Idle
    }

    private fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }
}

sealed class TranscriptionState {
    object Idle : TranscriptionState()
    data class Loading(val message: String) : TranscriptionState()
    data class Success(val result: String) : TranscriptionState()
    data class Error(val message: String) : TranscriptionState()
}
