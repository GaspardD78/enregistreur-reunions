package com.example.meetingrecorder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meetingrecorder.ui.theme.MeetingRecorderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeetingRecorderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenWithPermission()
                }
            }
        }
    }
}

@Composable
fun MainScreenWithPermission() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    ) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    if (hasPermission) {
        MainScreen()
    } else {
        RequestPermissionScreen {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}

@Composable
fun RequestPermissionScreen(onRequestPermission: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("L'application a besoin de l'accès au microphone pour enregistrer les réunions.")
        Button(onClick = onRequestPermission) {
            Text("Donner la permission")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val recordings by mainViewModel.recordings.collectAsState()
    val transcriptionState by mainViewModel.transcriptionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enregistreur de Réunions") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ApiConfigScreen()
            RecordingSection(
                onStartRecording = { mainViewModel.startRecording() },
                onStopRecording = { duration -> mainViewModel.stopRecording(duration) }
            )
            RecordingsList(
                recordings = recordings,
                onTranscribe = { recording -> mainViewModel.transcribeRecording(recording) }
            )
        }
    }

    when (val state = transcriptionState) {
        is TranscriptionState.Loading -> {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Transcription en cours") },
                text = {
                    Column {
                        CircularProgressIndicator()
                        Text(state.message)
                    }
                },
                confirmButton = {}
            )
        }
        is TranscriptionState.Success -> {
            AlertDialog(
                onDismissRequest = { mainViewModel.resetTranscriptionState() },
                title = { Text("Transcription terminée") },
                text = { Text(state.result) },
                confirmButton = {
                    TextButton(onClick = { mainViewModel.resetTranscriptionState() }) {
                        Text("OK")
                    }
                }
            )
        }
        is TranscriptionState.Error -> {
            AlertDialog(
                onDismissRequest = { mainViewModel.resetTranscriptionState() },
                title = { Text("Erreur de transcription") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { mainViewModel.resetTranscriptionState() }) {
                        Text("OK")
                    }
                }
            )
        }
        TranscriptionState.Idle -> {}
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MeetingRecorderTheme {
        MainScreen()
    }
}
