package com.example.meetingrecorder

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.meetingrecorder.ui.theme.MeetingRecorderTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecordingsList(
    recordings: List<Recording>,
    onTranscribe: (Recording) -> Unit
) {
    LazyColumn {
        items(recordings) { recording ->
            RecordingItem(recording, onTranscribe)
        }
    }
}

@Composable
fun RecordingItem(
    recording: Recording,
    onTranscribe: (Recording) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("📅 ${recording.date}")
                Text("⏱️ Durée: ${recording.duration}")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onTranscribe(recording) }) {
                Text("📝 Transcrire")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordingsListPreview() {
    val recordings = listOf(
        Recording(1, "2024-10-31 10:00:00", "00:15:30", ""),
        Recording(2, "2024-10-31 11:00:00", "00:25:10", "")
    )
    MeetingRecorderTheme {
        RecordingsList(recordings, {})
    }
}
