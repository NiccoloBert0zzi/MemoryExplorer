package com.example.memoryexplorer.ui.screens.statistics

import android.app.Application
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("DEPRECATION")
class StatisticsViewModel(
    private val application: Application,
    loginRepository: LoginRepository
) : ViewModel() {
    private val _locationsName =
        MutableStateFlow<List<Pair<String, Pair<String, String>>>>(emptyList())
    val locationsName: StateFlow<List<Pair<String, Pair<String, String>>>> = _locationsName

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var email: String? = null

    val pieData = mutableStateOf<PieData?>(null)

    init {
        viewModelScope.launch {
            email = loginRepository.email.first()
            getMemories()
        }
    }

    private fun getMemories() {
        _isLoading.value = true

        val database = FirebaseDatabase.getInstance().getReference("memories")
        database.get().addOnSuccessListener { dataSnapshot ->
            val locations = mutableListOf<Pair<String, Pair<String, String>>>()
            for (snapshot in dataSnapshot.children) {
                val memory = snapshot.getValue(Memory::class.java)
                if (memory != null && memory.creator == email) {
                    val geocoder = Geocoder(application, Locale.getDefault())
                    val addresses = try {
                        val lat = memory.latitude?.toDouble() ?: 0.0
                        val long = memory.longitude?.toDouble() ?: 0.0
                        geocoder.getFromLocation(lat, long, 1)
                    } catch (exception: NumberFormatException) {
                        _error.value = exception.localizedMessage
                        null
                    }
                    val countryName = addresses?.get(0)?.countryName
                    val adminName = addresses?.get(0)?.adminArea
                    val localityName = addresses?.get(0)?.locality
                    if (countryName != null && adminName != null && localityName != null) {
                        locations.add(Pair(countryName, Pair(adminName, localityName)))
                    }
                }
            }
            _locationsName.value = locations
        }.addOnFailureListener { exception ->
            _error.value = exception.localizedMessage
        }
    }

    fun generatePieData(selectedState: String): PieData {
        _isLoading.value = true

        val filteredLocations = when {
            selectedState == application.getString(R.string.world) -> {
                locationsName.value
            }

            locationsName.value.any { it.second.first == selectedState } -> {
                locationsName.value.filter { it.second.first == selectedState }
            }

            else -> {
                locationsName.value.filter { it.first == selectedState }
            }
        }

        val entries = when {
            selectedState == application.getString(R.string.world) -> {
                filteredLocations
                    .groupingBy { it.first }
                    .eachCount()
                    .map { PieEntry(it.value.toFloat(), it.key) }
            }

            locationsName.value.any { it.second.first == selectedState } -> {
                filteredLocations
                    .groupingBy { it.second.second }
                    .eachCount()
                    .map { PieEntry(it.value.toFloat(), it.key) }
            }

            else -> {
                filteredLocations
                    .groupingBy { it.second.first }
                    .eachCount()
                    .map { PieEntry(it.value.toFloat(), it.key) }
            }
        }

        val dataSet = PieDataSet(entries, selectedState)
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        dataSet.valueTextSize = 12f

        _isLoading.value = false
        return PieData(dataSet).apply { }
    }

    fun clearError() {
        _error.value = null
    }
}