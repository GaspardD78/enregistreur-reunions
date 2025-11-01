package com.example.meetingrecorder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.meetingrecorder.ui.theme.MeetingRecorderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiConfigScreen() {
    val context = LocalContext.current
    val apiKeyManager = remember { ApiKeyManager(context) }

    var assemblyAiApiKey by remember { mutableStateOf(apiKeyManager.getAssemblyAiApiKey() ?: "") }
    var mistralApiKey by remember { mutableStateOf(apiKeyManager.getMistralApiKey() ?: "") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("⚙️ Configuration des API")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = assemblyAiApiKey,
            onValueChange = { assemblyAiApiKey = it },
            label = { Text("Clé API AssemblyAI") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mistralApiKey,
            onValueChange = { mistralApiKey = it },
            label = { Text("Clé API Mistral AI") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                apiKeyManager.saveAssemblyAiApiKey(assemblyAiApiKey)
                apiKeyManager.saveMistralApiKey(mistralApiKey)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sauvegarder les clés")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApiConfigScreenPreview() {
    MeetingRecorderTheme {
        ApiConfigScreen()
    }
}
