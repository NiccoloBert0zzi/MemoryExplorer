package com.example.memoryexplorer.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    val email by profileViewModel.email.collectAsState()
    val memories by profileViewModel.memories.collectAsState()
    val favourites by profileViewModel.favourites.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val profileImage by profileViewModel.profileImage.collectAsState()

    var isMemory = true

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profileImage),
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
                Button(
                    onClick = {
                        navController.navigate(MemoryExplorerRoute.Statistics.route)
                    }
                ) {
                    Text(stringResource(R.string.statistics))
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = email?.split("@")?.get(0) ?: stringResource(R.string.username),
                    textAlign = TextAlign.Left
                )
            }
            Spacer(Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClickableText(
                    text = AnnotatedString(
                        text = stringResource(R.string.memories),
                        spanStyles = if (isMemory) listOf(
                            AnnotatedString.Range(
                                item = SpanStyle(textDecoration = TextDecoration.Underline),
                                start = 0,
                                end = stringResource(R.string.memories).length
                            )
                        ) else listOf()
                    ),
                    onClick = {
                        isMemory = true
                        profileViewModel.getMemories()
                        Log.d("ProfileScreen", "My memories: $isMemory")
                    }
                )
                ClickableText(
                    text = AnnotatedString(
                        text = stringResource(R.string.favorite),
                        spanStyles = if (!isMemory) listOf(
                            AnnotatedString.Range(
                                item = SpanStyle(textDecoration = TextDecoration.Underline),
                                start = 0,
                                end = stringResource(R.string.favorite).length
                            )
                        ) else listOf()
                    ),
                    onClick = {
                        isMemory = false
                        profileViewModel.getFavourites()
                        // TODO se non ci sono favoriti non entra nemmeno nella pagina, rimane in "My memories"
                        // TODO se apro una memory nella schermata preferiti, quando torno indietro mi ritrovo in "My memories"
                        Log.d("ProfileScreen", "My favourite: $isMemory")
                    }
                )
            }
            Spacer(Modifier.size(16.dp))
            Column {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    if (isMemory) {
                        if (memories.isEmpty()) {
                            NoMemoriesPlaceholder()
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 0.dp),
                                modifier = Modifier.padding(contentPadding)
                            ) {
                                items(memories) { memory ->
                                    MemoryItem(
                                        memory,
                                        onClick = {
                                            navController.navigate("${MemoryExplorerRoute.MemoryDetails.route}/${memory.id}")
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        if (favourites.isEmpty()) {
                            NoMemoriesPlaceholder()
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 0.dp),
                                modifier = Modifier.padding(contentPadding)
                            ) {
                                items(favourites) { fav ->
                                    MemoryItem(
                                        fav,
                                        onClick = {
                                            navController.navigate("${MemoryExplorerRoute.MemoryDetails.route}/${fav.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (error != null) {
                        Toast.makeText(navController.context, error, Toast.LENGTH_LONG).show()
                        profileViewModel.clearError()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryItem(
    memory: Memory,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .size(160.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = memory.image),
                "Memory picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.size(6.dp))
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            memory.title?.let { Text(it) }
        }
        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            memory.date?.let { Text(it) }
        }
        Spacer(Modifier.size(10.dp))
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