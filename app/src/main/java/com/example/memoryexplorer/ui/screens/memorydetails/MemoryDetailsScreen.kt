package com.example.memoryexplorer.ui.screens.memorydetails

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.example.memoryexplorer.ui.composables.AppBar
import org.osmdroid.views.MapView

@Composable
fun MemoryDetailsScreen(
    navController: NavHostController,
    memoryId: String?,
    memoryDetailsViewModel: MemoryDetailsViewModel,
) {
    if (memoryId != null) {
        memoryDetailsViewModel.run { getMemoryById(memoryId) }
    }
    val memory = memoryDetailsViewModel.memory.value ?: return
    val isLoading by memoryDetailsViewModel.isLoading.collectAsState()
    val error by memoryDetailsViewModel.error.collectAsState()
    val favouritesState by memoryDetailsViewModel.state.collectAsState()
    val favourites = favouritesState.favourites

    Scaffold(
        topBar = {
            AppBar(
                navController,
                MemoryExplorerRoute.MemoryDetails,
                memory.title
            )
        },
        modifier = Modifier.fillMaxSize(),
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
        } else {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = memory.image),
                        "Memory picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    memory.title?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        if (favourites.contains(memory.id?.let { Favourite(it) })) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Memory star icon",
                        modifier = Modifier.clickable {
                            if (favourites.contains(memory.id?.let { Favourite(it) })) {
                                memory.id?.let { memoryDetailsViewModel.removeFavourite(it) }
                            } else {
                                memory.id?.let { memoryDetailsViewModel.addFavourite(it) }
                            }
                        }
                    )
                }
                Spacer(Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    memory.date?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    memory.description?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.size(50.dp))
                OsmMapView(memoryDetailsViewModel, memory, LocalContext.current)
            }
            if (error != null) {
                Toast.makeText(navController.context, error, Toast.LENGTH_LONG).show()
                memoryDetailsViewModel.clearError()
            }
        }
    }
}

@Composable
fun OsmMapView(memoryDetailsViewModel: MemoryDetailsViewModel, memory: Memory, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    memoryDetailsViewModel.openMap(memory.latitude!!, memory.longitude!!, this, context)
                }
            },
        )
    }
}
