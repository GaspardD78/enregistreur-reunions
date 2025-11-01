package com.example.meetingrecorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ApiService(
    private val assemblyaiApiKey: String,
    private val mistralApiKey: String
) {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun uploadFile(file: File): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.assemblyai.com/v2/upload")
            .header("authorization", assemblyaiApiKey)
            .post(file.asRequestBody("application/octet-stream".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        val uploadResponse = json.decodeFromString<UploadResponse>(responseBody)
        uploadResponse.upload_url
    }

    suspend fun createTranscript(audioUrl: String): String = withContext(Dispatchers.IO) {
        val requestBody = """
            {
                "audio_url": "$audioUrl",
                "language_code": "fr",
                "speaker_labels": true
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.assemblyai.com/v2/transcript")
            .header("authorization", assemblyaiApiKey)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        val transcriptResponse = json.decodeFromString<TranscriptResponse>(responseBody)
        transcriptResponse.id
    }

    suspend fun getTranscript(transcriptId: String): Transcript = withContext(Dispatchers.IO) {
        while (true) {
            val request = Request.Builder()
                .url("https://api.assemblyai.com/v2/transcript/$transcriptId")
                .header("authorization", assemblyaiApiKey)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val transcript = json.decodeFromString<Transcript>(responseBody)

            if (transcript.status == "completed" || transcript.status == "error") {
                return@withContext transcript
            }

            delay(3000)
        }
        // Should not be reached
        throw IllegalStateException("Polling loop exited unexpectedly")
    }

    suspend fun getSummary(text: String): String = withContext(Dispatchers.IO) {
        val requestBody = """
            {
                "model": "mistral-tiny",
                "messages": [
                    {
                        "role": "user",
                        "content": "Tu es un assistant qui rédige des comptes rendus de réunion.\n\nVoici la transcription d'une réunion :\n\n$text\n\nRédige un compte rendu structuré avec :\n1. Titre\n2. Résumé exécutif (3-4 phrases)\n3. Points clés discutés\n4. Décisions prises\n5. Actions à mener\n\nFormat professionnel et concis en français."
                    }
                ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.mistral.ai/v1/chat/completions")
            .header("Authorization", "Bearer $mistralApiKey")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        val summaryResponse = json.decodeFromString<SummaryResponse>(responseBody)
        return@withContext summaryResponse.choices.first().message.content
    }
}

@Serializable
data class UploadResponse(val upload_url: String)

@Serializable
data class TranscriptResponse(val id: String)

@Serializable
data class Transcript(val status: String, val text: String?, val utterances: List<Utterance>?)

@Serializable
data class Utterance(val speaker: String, val text: String)

@Serializable
data class SummaryResponse(val choices: List<Choice>)

@Serializable
data class Choice(val message: Message)

@Serializable
data class Message(val content: String)
