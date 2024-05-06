package com.example.memoryexplorer.ui.screens.statistics

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    statisticsViewModel: StatisticsViewModel
) {
    val isLoading by statisticsViewModel.isLoading.collectAsState()
    val error by statisticsViewModel.error.collectAsState()
    val locationsName by statisticsViewModel.locationsName.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(navController.context.getString(R.string.world)) }
    var selectedState = navController.context.getString(R.string.world)
    var state = true

    statisticsViewModel.updatePieData(selectedState)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        } else if (locationsName.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.secondary,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                                ),
                                value = selectedText,
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    { Text(text = navController.context.getString(R.string.world)) },
                                    onClick = {
                                        selectedText =
                                            navController.context.getString(R.string.world)
                                        expanded = false
                                        state = true
                                        statisticsViewModel.updatePieData(navController.context.getString(R.string.world))
                                    }
                                )
                                val addedItems = mutableSetOf<String>()
                                locationsName.forEach { item ->
                                    if (state && addedItems.add(item.first)) {
                                        DropdownMenuItem(
                                            { Text(text = item.first) },
                                            onClick = {
                                                selectedText = item.first
                                                expanded = false
                                                state = false
                                                selectedState = item.first
                                                statisticsViewModel.updatePieData(selectedState)
                                            }
                                        )
                                    } else if (!state && addedItems.add(item.second.first) && item.first == selectedState) {
                                        DropdownMenuItem(
                                            { Text(text = item.second.first) },
                                            onClick = {
                                                selectedText = item.second.first
                                                expanded = false
                                                statisticsViewModel.updatePieData(selectedText)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (statisticsViewModel.pieData.value != null) {
                        PieChartComposable(statisticsViewModel.pieData.value!!)
                    }
                }
                if (error != null) {
                    Toast.makeText(navController.context, error, Toast.LENGTH_LONG).show()
                    statisticsViewModel.clearError()
                }
            }
        } else {
            NoMemoriesPlaceholder()
        }
    }
}

@Composable
fun NoMemoriesPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.Image,
            contentDescription = "Image icon",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            stringResource(R.string.no_memories),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun PieChartComposable(pieData: PieData) {
    var oldPieData by remember { mutableStateOf<PieData?>(null) }
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                legend.isEnabled = false
                animateY(1400)
            }
        },
        update = { pieChart ->
            if (oldPieData != pieData) {
                pieChart.animateY(1400)
                oldPieData = pieData
            }
            pieChart.data = pieData
            pieChart.invalidate()
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}