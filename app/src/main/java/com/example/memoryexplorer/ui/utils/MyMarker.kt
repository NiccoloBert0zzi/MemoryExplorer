package com.example.memoryexplorer.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.memoryexplorer.R

class MyMarker(context: Context) {
    private var smallMarker: Bitmap? = null

    init {
        val width = 100
        val height = 100
        val b = BitmapFactory.decodeResource(context.resources, R.drawable.marker_icon)
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
    }

    fun getSmallMarker(): Bitmap? {
        return smallMarker
    }
}