package com.example.meetingrecorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meetingrecorder.ui.theme.MeetingRecorderTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordingSection(
    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    DisposableEffect(isRecording) {
        val job = if (isRecording) {
            scope.launch {
                while (true) {
                    delay(1000)
                    duration++
                }
            }
        } else {
            null
        }
        onDispose {
            job?.cancel()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatDuration(duration),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onStartRecording()
                isRecording = true
                duration = 0
            },
            enabled = !isRecording
        ) {
            Text("Démarrer l'enregistrement")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                onStopRecording(duration * 1000)
                isRecording = false
            },
            enabled = isRecording
        ) {
            Text("Arrêter l'enregistrement")
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
}

@Preview(showBackground = true)
@Composable
fun RecordingSectionPreview() {
    MeetingRecorderTheme {
        RecordingSection({}, {})
    }
}
