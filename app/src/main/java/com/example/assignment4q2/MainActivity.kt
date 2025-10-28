package com.example.assignment4q2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment4q2.ui.theme.Assignment4Q2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.material3.TopAppBar

class CounterViewModel: ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    private val _autoMode = MutableStateFlow(false)
    val autoMode: StateFlow<Boolean> = _autoMode

    private val _interval = MutableStateFlow(3000L)
    val interval: StateFlow<Long> = _interval

    fun increment() { _count.value += 1}
    fun decrement() { _count.value -= 1}
    fun reset() { _count.value = 0}
    fun toggleAuto() { _autoMode.value = !_autoMode.value}
    fun setInterval(ms: Long) { _interval.value = ms}

    init {
        viewModelScope.launch {
            while (true) {
                delay(_interval.value)
                if (_autoMode.value) _count.value += 1
            }
        }
    }
}
class MainActivity : ComponentActivity() {
    private val viewModel: CounterViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment4Q2Theme {
                var showSettings by remember {mutableStateOf(false)}
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (showSettings) "Settings" else "Counter++") },
                            actions = {
                                TextButton(onClick = { showSettings = !showSettings }) {
                                    Text(if (showSettings) "Back" else "Settings")
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (showSettings) {
                        SettingsScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        CounterScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CounterScreen(viewModel: CounterViewModel, modifier: Modifier = Modifier) {
    val count by viewModel.count.collectAsState()
    val autoMode by viewModel.autoMode.collectAsState()

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Count: $count", style = MaterialTheme.typography.headlineMedium)
        Text("Auto mode: ${if(autoMode) "ON" else "OFF"}", style = MaterialTheme.typography.bodyLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.increment() }) { Text("+1") }
            Button(onClick = { viewModel.decrement() }) { Text("-1") }
            Button(onClick = { viewModel.reset() }) { Text("Reset") }
            Button(onClick = { viewModel.toggleAuto() }) { Text("Auto") }
        }
    }
}



@Composable
fun SettingsScreen(viewModel: CounterViewModel, modifier: Modifier = Modifier) {
    val interval by viewModel.interval.collectAsState()
    var input by remember { mutableStateOf(interval.toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Auto Increment Interval (ms):", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Interval in milliseconds") }
        )
        Button(
            onClick = {
                input.toLongOrNull()?.let { viewModel.setInterval(it) }
            }
        ) {
            Text("Apply")
        }

        Text("Current interval: ${interval} ms")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assignment4Q2Theme {

    }
}